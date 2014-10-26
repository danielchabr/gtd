package GTD.DL.DLEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


/**
 * Trída predstavuje surovou cinnost tak, jak ji uživatel vymyslí, bez dalšího
 * clenení - je pripravena na prevedení do úkolu nebo projektu.
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:50
 */
@Entity
public class Activity extends Action {

	/**
	 * stav činnosti
	 */
	@ManyToOne
	private ActivityState stav;
	
	public Activity(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * Konstruktor cinnosti
	 * 
	 * @param nazev
	 * @param popis
	 * @param stav
	 * @param vlastnik_id    vlastnik_id
	 */
	public Activity(String nazev, String popis, int stav, int vlastnik_id){

	}

	/**
	 * Konstruktor cinnosti
	 * 
	 * @param id
	 * @param nazev
	 * @param popis
	 * @param stav
	 * @param stavPopis
	 * @param vlastnik_id    vlastnik_id
	 */
	public Activity(int id, String nazev, String popis, int stav, String stavPopis, int vlastnik_id){

	}

	public ActivityState getStav()
	{
		return stav;
	}

	public void setStav(ActivityState stav)
	{
		this.stav = stav;
	}
	
	

}