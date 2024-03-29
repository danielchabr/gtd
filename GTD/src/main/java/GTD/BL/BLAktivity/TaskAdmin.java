package GTD.BL.BLAktivity;

import GTD.DL.DLInterfaces.IDAOTask;
import GTD.BL.BLOsoby.PersonAdmin;
import GTD.DL.DLDAO.DAOState;
import GTD.DL.DLDAO.DAOTask;
import GTD.DL.DLDAO.ItemNotFoundException;
import GTD.DL.DLEntity.Activity;
import GTD.DL.DLEntity.ActivityState;
import GTD.DL.DLEntity.Task;
import GTD.DL.DLEntity.Context;
import GTD.DL.DLEntity.Person;
import GTD.DL.DLEntity.Project;
import GTD.DL.DLInterfaces.IDAOState;
import java.util.List;
import javax.security.auth.login.LoginException;

/**
 * Trída pro manipulaci s úkoly.
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:55
 */
public class TaskAdmin {

	private IDAOTask daoTask;
	/**
	 * Odkaz na ActivitiyAdmin - při přidání úkolu vzniklého z činnosti je třeba tuto
	 * činnost označit jako "zpracovanou" - to zařídí právě ActivitiyAdmin.
	 */
	private ActivitiyAdmin activityAdmin;
	/**
	 * Správce osob - pomocí něj přistupují ostatní správci k přihlášenému uživateli.
	 */
	private PersonAdmin personAdmin;
	
	private IDAOState daoState;



	public void finalize() throws Throwable {

	}

	public TaskAdmin(){

	}

	public TaskAdmin(IDAOTask daoTask, ActivitiyAdmin activityAdmin, PersonAdmin personAdmin, IDAOState daoState)
	{
		this.daoTask = daoTask;
		this.activityAdmin = activityAdmin;
		this.personAdmin = personAdmin;
		this.daoState = daoState;
	}
	
	

	public void setDaoTask(IDAOTask daoTask)
	{
		this.daoTask = daoTask;
	}

	public void setActivityAdmin(ActivitiyAdmin activityAdmin)
	{
		this.activityAdmin = activityAdmin;
	}

	public void setPersonAdmin(PersonAdmin personAdmin)
	{
		this.personAdmin = personAdmin;
	}

	public void setDaoState(IDAOState daoState)
	{
		this.daoState = daoState;
	}

	
	
	
	/**
	 * Vytvoří nový úkol. Úkol v projektu může vytvořit pouze vlastník tohoto projektu.
	 * 
	 * @param user logged-in user
	 * @return
	 * 
	 * @param ukol
	 * @param cinnost    Činnost, ze které úkol vznikl (pokud existuje) - používá se
	 * pro označení činnosti jako "zpracované".
	 */
	public void addUkol(Task ukol, Person user, Activity cinnost){
		boolean isProjectOwner = ukol.getProject() == null ? true : ukol.getProject().getOwner().getId() == user.getId();
		boolean isActivityOwner = cinnost == null ? true : ukol.getOwner().getId() == cinnost.getOwner().getId();
		if (isProjectOwner && isActivityOwner) {
			ukol.setState(daoState.getUkolVytvoreny());
			ukol.setCreator(user);
			ukol.setOwner(user); // don't care if owner or creator is already set - maybe not?
			daoTask.create(ukol);
			if (cinnost != null) activityAdmin.processCinnost(cinnost, user); // TODO steklsim pokud processCinnost() hodi vyjimku, nemel by se zrusit ukol?
		// TODO steklsim tady hodit nejakou Spring exception? (az bude Spring)
		} else {
			throw new SecurityException("User '" + user.getLogin()
				+ "' can't add a task to a project owned by '" 
				+ ukol.getOwner().getLogin() + "'");
		}
	}

	/**
	 * Smaže úkol (resp. označí jako smazaný). Mazat úkol může pouze jeho vlastník a
	 * vlastník nadřazeného projektu (v 1.úrovni).
	 * @param user logged-in user
	 * @return
	 * 
	 * @param ukol
	 */
	public void deleteUkol(Task ukol, Person user){
		if (ukol.getOwner().getId() == user.getId() 
				|| (ukol.getProject() != null && ukol.getProject().getOwner().getId() == user.getId())) {
			daoTask.delete(ukol);
		} else {
			throw new SecurityException("Task owned by '" 
				+ ukol.getOwner().getLogin() + "' can't be deleted by '" 
				+ user.getLogin() + "'");
		}
		
	}

	/**
	 * Vrátí všechny úkoly
	 * @return
	 */
	public List<Task> getAllUkoly(){
		// if user has role ROLE_ADMIN
		return daoTask.getAll();
	}

	/**
	 * Vrátí úkol.
	 * @param user logged-in user
	 * @return
	 * 
	 * @param id
	 */
	public Task getUkol(int id, Person user){
		Task ukol = daoTask.get(id);
		if (ukol != null) {
			int taskOwnerId = ukol.getOwner().getId();
			Project project = ukol.getProject();
			if ((taskOwnerId != user.getId() && project == null)
				|| (taskOwnerId != user.getId() && ukol.getProject().getOwner().getId() != user.getId())) { // TODO steklsim authorization not needed here maybe?
				throw new SecurityException("Task owned by '" 
						+ ukol.getOwner() + "' can't be read by '" 
						+ user);
			}
		}
		return ukol;
	}

	/**
	 * Vrátí všechny úkoly daného kontextu.
	 * @param user logged-in user
	 * @return
	 * 
	 * @param kontext
	 */
	public List getUkolyKontextu(Context kontext, Person user){
		if (kontext.getVlastnik().getId() == user.getId()) {
			return daoTask.getUkolyKontextu(kontext);	
		} else {
			throw new SecurityException("User '" + user.getLogin() 
					+ "' can't access context owned by '"
					+ kontext.getVlastnik().getLogin() + "'");
		}
	}

	/**
	 * @return
	 * 
	 * @param user logged-in user
	 */
	public List getUkolyOsoby(Person user){
		return daoTask.getUkolyOsoby(user);
	}

	/**
	 * Nastaví kontext úkolu. Toto může udělat pouze vlastník úkolu.
	 * @param user logged-in user
	 * @return
	 * 
	 * @param ukol
	 * @param kontext
	 */
	public void setKontext(Task ukol, Context kontext, Person user){
		if (ukol.getOwner().getId() == kontext.getVlastnik().getId()
				&& ukol.getOwner().getId() == user.getId()) {
			ukol.setContext(kontext);
			daoTask.update(ukol);
		// TODO steklsim tady hodit nejakou Spring exception? (az bude Spring)
		} else {
			throw new SecurityException("Task owned by '" 
				+ ukol.getOwner().getLogin() + "' can't be altered by '" 
				+ user.getLogin() + "'");
		}
	}

	/**
	 * Uloží změněný úkol (změna názvu/popisu). Změnit úkol může  jeho vlastník nebo
	 * vlastník nadřazeného projektu (v 1.úrovni).
	 * @param user logged-in user
	 * @return
	 * 
	 * @param ukol
	 */
	public void updateUkol(Task ukol, Person user)
	{ 
		Task oldTask = daoTask.get(ukol.getId());
//		if (oldTask == null) throw new ItemNotFoundException("Task with id '" + ukol.getId() + "' not found");
		oldTask.update(ukol);
		
		if (oldTask.getOwner().getId() == user.getId()
				|| (oldTask.getProject() != null && oldTask.getProject().getOwner().getId() == user.getId())) {
			daoTask.update(oldTask);
		} else {
			throw new SecurityException("Task owned by '" 
				+ ukol.getOwner().getLogin() + "' can't be updated by '" 
				+ user.getLogin() + "'");
		}
	}

}