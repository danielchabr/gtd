package GTD.BL.BLAktivity;

import GTD.DL.DLInterfaces.IDAOProject;
import GTD.BL.BLOsoby.PersonAdmin;
import GTD.DL.DLEntity.Activity;
import GTD.DL.DLEntity.Project;
import GTD.DL.DLEntity.Person;
import GTD.DL.DLEntity.ProjectState;
import GTD.DL.DLInterfaces.IDAOState;
import java.util.List;

/**
 * Trída pro manipulaci s projekty.
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:55
 */
public class ProjectAdmin {

	private IDAOProject DAOProjekt;
	private IDAOState DAOStav;
	/**
	 * Odkaz na ActivitiyAdmin - při přidání projektu vzniklého z činnosti je třeba
	 * tuto činnost označit jako "zpracovanou" - to zařídí právě ActivitiyAdmin.
	 */
	private ActivitiyAdmin spravceCinnosti;
	/**
	 * Správce osob - pomocí něj přistupují ostatní správci k přihlášenému uživateli.
	 */
	private PersonAdmin spravceOsob;



	public void finalize() throws Throwable {

	}

	public ProjectAdmin(){

	}

	public ProjectAdmin(IDAOProject DAOProjekt, IDAOState DAOStav, ActivitiyAdmin spravceCinnosti, PersonAdmin spravceOsob)
	{
		this.DAOProjekt = DAOProjekt;
		this.DAOStav = DAOStav;
		this.spravceCinnosti = spravceCinnosti;
		this.spravceOsob = spravceOsob;
	}
	
	

	public void setDAOProjekt(IDAOProject DAOProjekt)
	{
		this.DAOProjekt = DAOProjekt;
	}

	public void setSpravceCinnosti(ActivitiyAdmin spravceCinnosti)
	{
		this.spravceCinnosti = spravceCinnosti;
	}

	public void setDAOStav(IDAOState DAOStav)
	{
		this.DAOStav = DAOStav;
	}

	
	
	
	/**
	 * Vytvorí nový projekt zadaných vlastností a uloží ho do databáze. Podprojekt v
	 * projektu může vytvořit pouze vlastník tohoto nadřazeného projektu.
	 * @param user logged-in user
	 * @return
	 * 
	 * @param projekt
	 * @param cinnost    Činnost, ze které projekt vznikl (pokud existuje) - používá
	 * se pro označení činnosti jako "zpracované".
	 */
	public void addProjekt(Project projekt, Activity cinnost, Person user){
		boolean isActivityOwner = cinnost == null ? true : user.getId() == cinnost.getOwner().getId();
		boolean isParentOwner = projekt.getRodic() == null ? true : user.getId() == projekt.getRodic().getOwner().getId();
		
//		if (projekt.getOwner().getId() == cinnost.getOwner().getId()
//				&& projekt.getOwner().getId() == user.getId()
//				&& projekt.getRodic().getOwner().getId() == user.getId()) {
		if (isActivityOwner && isParentOwner) {
			projekt.setStav(DAOStav.getProjektAktivni());
			projekt.setOwner(user);
			DAOProjekt.create(projekt);
			if (cinnost != null) spravceCinnosti.processCinnost(cinnost, user); // TODO steklsim pokud processCinnost() hodi vyjimku, nemel by se zrusit projekt?
		} else {
			throw new SecurityException("Project owned by '" 
				+ projekt.getOwner().getLogin() + "' can't be added by '" 
				+ user.getLogin() + "'");
		}
	}

	/**
	 * Smaže projekt (resp. označí jako smazaný) z databáze spolu se všemi jeho úkoly
	 * a podprojekty. Project může mazat jeho vlastník nebo vlastník nadřazeného
	 * projektu (v 1.úrovni).
	 * @param user logged-in user
	 * @return
	 * 
	 * @param projekt
	 */
	public void deleteProjekt(Project projekt, Person user){ // TODO steklsim v docBlocku je projekt "oznacen jako smazany" - nemame na to stav
		if (projekt.getOwner().getId() == user.getId()
				|| projekt.getRodic().getOwner().getId() == user.getId()) {
			DAOProjekt.delete(projekt);
		} else {
			throw new SecurityException("Project owned by '" 
				+ projekt.getOwner().getLogin() + "' can't be deleted by '" 
				+ user.getLogin() + "'");
		}
	}

	/**
	 * Označí projekt jako "dokončený". Dokončit projekt může jeho vlastník nebo
	 * vlastník nadřazeného projektu (v 1.úrovni).
	 * @param user logged-in user
	 * @return
	 * 
	 * @param projekt
	 */
	public void finishProjekt(Project projekt, Person user){
		if (projekt.getOwner().getId() == user.getId()
				|| projekt.getRodic().getOwner().getId() == user.getId()) {
			ProjectState dokonceny = DAOStav.getProjektDokonceny();
			projekt.setStav(dokonceny);
			DAOProjekt.update(projekt);
		} else {
			throw new SecurityException("Project '" 
				+ projekt.getTitle() + "' can't be marked as finished by '" 
				+ user.getLogin() + "'");
		}
	}

	/**
	 * Vrátí všechny projekty
	 * @return
	 */
	public List<Project> getAllProjekty(){
		// if user has role ROLE_ADMIN
		return DAOProjekt.getAll();
	}

	/**
	 * Vrátí projekt podle jeho ID.
	 * @param user logged-in user
	 * @return
	 * 
	 * @param id
	 */
	public Project getProjekt(int id, Person user){
		Project projekt = DAOProjekt.get(id);
		if (projekt != null 
				&& !(projekt.getOwner().getId() == user.getId())
				&& !(projekt.getRodic().getOwner().getId() == user.getId())) {
			throw new SecurityException("Project '" 
					+ projekt.getTitle() + "' can't be marked as finished by '" 
					+ user.getLogin() + "'");
		}
		return projekt;
	}

	/**
	 * Vrátí všechny projekty patrící zadané osobe.
	 * @return
	 * 
	 * @param user logged-in user
	 */
	public List<Project> getProjektyOsoby(Person user){
		return DAOProjekt.getProjektyOsoby(user);
	}

	/**
	 * Uloží změněný projekt (změna názvu/popisu). Měnit projekt může  jeho vlastník
	 * nebo vlastník nadřazeného projektu (v 1.úrovni).
	 * @param user logged-in user
	 * @return
	 * 
	 * @param projekt
	 */
	public void updateProjekt(Project projekt, Person user){ // TODO steklsim metoda zatim na nic
		Project oldProject = DAOProjekt.get(projekt.getId());
		oldProject.update(projekt);
		
		if (oldProject.getOwner().getId() == user.getId()
				|| (oldProject.getRodic() != null && oldProject.getRodic().getOwner().getId() == user.getId())) {
			DAOProjekt.update(oldProject);
		} else {
			throw new SecurityException("Project '" 
				+ oldProject.getTitle() + "' can't be updated by '" 
				+ user.getLogin() + "'");
		}
	}
	
}