package GTD.DL.DLDAO;

import GTD.DL.DLEntity.Activity;
import GTD.DL.DLEntity.Context;
import GTD.DL.DLEntity.Person;
import GTD.DL.DLInterfaces.IDAOContext;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Trída zapouzdruje metody pro ukládání a nacítání kontextu z databáze.
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:51
 */
public class DAOContext extends DAOGeneric<Context> implements IDAOContext {



	public void finalize() throws Throwable {

	}

	/**
	 * Kontruktor kontextu
	 */
	public DAOContext(){

	}

//	/**
//	 * Vytvorí nový kontext zadaných vlastností a uloží ho do databáze.
//	 * @return
//	 * 
//	 * @param kontext
//	 */
//	public boolean createKontext(Context kontext){
//		return false;
//	}
//
//	/**
//	 * Smaže kontext z databáze.
//	 * @return
//	 * 
//	 * @param kontext
//	 */
//	public boolean deleteKontext(Context kontext){
//		return false;
//	}
//
//	/**
//	 * Vrátí všechny kontexty v systému.
//	 * @return List<Kontext>
//	 */
//	public List getAllKontexty(){
//		return null;
//	}
//
//	/**
//	 * Vrátí kontext podle jeho ID.
//	 * @return kontext
//	 * 
//	 * @param id
//	 */
//	public Context getKontext(int id){
//		return null;
//	}

	/**
	 * Vrátí všechny kontexty patrící zadané osobe.
	 * @return List<Kontext>
	 * 
	 * @param osoba
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List getKontextyOsoby(Person osoba){
		Session session = this.openSession();
		Query query = session.createQuery(
				"from " + Context.class.getName() + " c " + 
				"where c.vlastnik = :vlastnik"
		);
		query.setParameter("vlastnik", osoba);
		List<Context> contexts = (List<Context>) query.list();
		session.close();
		
		return contexts;
	}

//	/**
//	 * Uloží zmenený kontext.
//	 * @return
//	 * 
//	 * @param kontext
//	 */
//	public boolean updateKontext(Context kontext){
//		return false;
//	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Context> getAll()
	{
		return (List<Context>) this.getAll(Context.class);
	}

	@Override
	public Context get(int id)
	{
		return (Context) this.get(Context.class, id);
	}

}