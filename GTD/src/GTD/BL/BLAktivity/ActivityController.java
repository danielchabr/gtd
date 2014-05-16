package GTD.BL.BLAktivity;
import GTD.BL.BLInterfaces.IActivityController;
import GTD.DL.DLDAO.DAOState;
import GTD.DL.DLEntity.Activity;
import GTD.DL.DLEntity.Person;
import GTD.DL.DLInterfaces.IDAOState;
import GTD.PL.PLView.GTDGUI;
import java.util.List;

/**
 * Třída implementuje interface IActivityController.
 * @author GTD team
 * @version 1.0
 */
public class ActivityController implements IActivityController {

	private ActivitiyAdmin activityAdmin;
	private IDAOState daoState;

	/**
	 *
	 */
	public ActivityController(){
		activityAdmin = new ActivitiyAdmin();
		daoState = new DAOState();
	}

	/**
	 * Přidá novou činnost zadaných vlastností.
	 * 
	 * @param nazev
	 * @param popis
	 */
	@Override
	public boolean addActivity(String nazev, String popis){
		Activity newCinnost = new Activity(nazev, popis, daoState.getActivityForProcessingID(), GTDGUI.getMyself().getId());
		return activityAdmin.addActivity(newCinnost);
	}

	/**
	 * Smaže činnost.
	 * 
	 * @param cinnost
	 */
	@Override
	public boolean deleteActivity(Activity cinnost){
		return activityAdmin.deleteActivity(cinnost);
	}

	/**
	 * Vrátí činnosti konkrétní osoby.
	 * 
	 * @param osoba
	 */
	@Override
	public List<Activity> getActivitiesOfPerson(Person osoba){
		return activityAdmin.getActivitiesOfPerson(osoba);
	}

	/**
	 * Označí činnost jako "zpracovanou".
	 * 
	 * @param cinnost
	 */
	@Override
	public boolean processActivity(Activity cinnost){
		return false;
	}

	/**
	 * Označí činnost jako "archivovanou".
	 * 
	 * @param cinnost
	 */
	@Override
	public boolean archiveActivity(Activity cinnost){
		return activityAdmin.archiveActivity(cinnost);
	}

	/**
	 * Uloží změněnou činnost (změna jména/popisu).
	 * 
	 * @param cinnost
	 */
	@Override
	public boolean updateActivity(Activity cinnost){
		return false;
	}

	/**
	 * Odešle GUI pokyn k obnovení.
	 */
	@Override
	public void refresh(){
		GTDGUI.getGTDGUI().refresh();
	}

	/**
	 * Označí činnost jako "odloženou".
	 * 
	 * @param cinnost
	 */
	@Override
	public boolean postponeActivity(Activity cinnost){
		return activityAdmin.postponeActivity(cinnost);
	}

}