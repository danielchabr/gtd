package GTD.DL.DLEntity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Trída predstavuje casový interval pro daný úkol.
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:53
 */
@Entity
@Table(name = "intervals")
public class Interval {

	@Id
	@GeneratedValue
	private int id;
	
	/**
	 * Casový pocátek úkolu. Minimální presnost jsou dny.
	 */
	@Column(name = "from_date", nullable = false)
	private Date from;
	/**
	 * Casový konec úkolu. Minimální presnost jsou dny.
	 */
	@Column(name = "to_date", nullable = false)
	private Date to;


	
	public void finalize() throws Throwable {

	}

	/**
	 * Nastavení hodnot intervalu
	 * 
	 * @param from
	 * @param to    to
	 */
	public Interval(Date from, Date to){

	}

	/**
	 * Konstruktor intevalu
	 */
	public Interval(){

	}

	
	public int getId()
	{
		return id;
	}
	
	/**
	 * Vrátí datum do z intervalu
	 * @return from
	 */
	public Date getFrom(){
		return from;
	}

	/**
	 * Vrátí datum od z intervalu
	 * @return to
	 */
	public Date getTo(){
		return to;
	}

	/**
	 * Vrátí tru, pikud je interval nastaven
	 * @return
	 */
	public boolean isSet(){
		throw new UnsupportedOperationException();
	}

	/**
	 * Nastav interval
	 * 
	 * @param from
	 * @param to    to
	 */
	public void setInterval(Date from, Date to){
		this.from = from;
		this.to = to;
	}

	public void setFrom(Date from)
	{
		this.from = from;
	}

	public void setTo(Date to)
	{
		this.to = to;
	}
	
	

}