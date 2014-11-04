package GTD.DL.DLDAO;

import GTD.DL.DLEntity.Activity;
import GTD.DL.DLEntity.Person;
import GTD.DL.DLInterfaces.IDAOActivity;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Trída zapouzdruje metody pro ukládání a nacítání cinností z databáze.
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:51
 */
public class DAOActivity extends DAOGeneric<Activity> implements IDAOActivity {



	public void finalize() throws Throwable {

	}

	/**
	 * Konstruktor činnosti
	 */
	public DAOActivity(){

	}

//	/**
//	 * Vytvorí novou cinnost zadaných vlastností a uloží ji do databáze.
//	 * @return
//	 * 
//	 * @param cinnost
//	 */
//	public boolean createCinnost(Activity cinnost){
//		return false;
//	}
//
//	/**
//	 * Smaže cinnost z databáze.
//	 * @return
//	 * 
//	 * @param cinnost
//	 */
//	public boolean deleteCinnost(Activity cinnost){
//		return false;
//	}
//
//	/**
//	 * Vrátí všechny cinnosti v systému.
//	 * @return List<Cinnost>
//	 */
//	public List getAllCinnosti(){
//		return null;
//	}
//
//	/**
//	 * Vrátí cinnost podle jejího ID.
//	 * @return cinnost
//	 * 
//	 * @param id
//	 */
//	public Activity getCinnost(int id){
//		return null;
//	}
//
	/**
	 * Vrátí všechny cinnosti patrící zadané osobe.
	 * @return List<Cinnost>
	 * 
	 * @param osoba
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Activity> getCinnostiOsoby(Person osoba){
		Session session = this.openSession();
		Query query = session.createQuery(
				"from " + Activity.class.getName() + " a " + 
				"where a.vlastnik = :vlastnik"
		);
		query.setParameter("vlastnik", osoba);
		List<Activity> activities = (List<Activity>) query.list();
		session.close();
		
		return activities;
	}
//
//	/**
//	 * Uloží zmenenou cinnost.
//	 * @return
//	 * 
//	 * @param cinnost
//	 */
//	public boolean updateCinnost(Activity cinnost){
//		return false;
//	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Activity> getAll()
	{
		return (List<Activity>) this.getAll(Activity.class);
	}

	@Override
	public Activity get(int id)
	{
		return (Activity) this.get(Activity.class, id);
	}

	
	
	
}