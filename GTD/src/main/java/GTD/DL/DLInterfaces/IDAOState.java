package GTD.DL.DLInterfaces;

import GTD.DL.DLEntity.ActivityState;
import GTD.DL.DLEntity.ContactType;
import GTD.DL.DLEntity.PersonState;
import GTD.DL.DLEntity.ProjectState;
import GTD.DL.DLEntity.TaskState;


/**
 * Interface pro získání typů
 * @author Šimon
 * @version 1.0
 * @created 19-10-2014 12:30:53
 */
public interface IDAOState {

	/**
	 * Vrátí stav: činnost Archivovaná
	 * @return
	 */
	public ActivityState getCinnostArchivovana();

	/**
	 * Vrátí stav: činost Ke zpracování
	 * @return
	 */
	public ActivityState getCinnostKeZpracovani();

	/**
	 * Vrátí stav: činost Odlozena
	 * @return
	 */
	public ActivityState getCinnostOdlozena();

	/**
	 * Vrátí stav: činost Zahozena
	 * @return
	 */
	public ActivityState getCinnostZahozena();

	/**
	 * Vrátí stav: činost Odlozena
	 * @return
	 */
	public ActivityState getCinnostZpracovana();

	/**
	 * Vrátí stav: konatakt email
	 * @return
	 */
	public ContactType getKontaktEmail();

	/**
	 * Vrátí stav: konatakt telefon
	 * @return
	 */
	public ContactType getKontaktTelefon();

	/**
	 * Vrátí stav: osoby Aktivni
	 * @return
	 */
	public PersonState getOsobaAktivni();

	/**
	 * Vrátí stav: osoby Aktivni
	 * @return
	 */
	public PersonState getOsobaNeaktivni();

	/**
	 * Vrátí stav: projekt Aktivni
	 * @return
	 */
	public ProjectState getProjektAktivni();

	/**
	 * Vrátí stav: projekt Dokonceny
	 * @return
	 */
	public ProjectState getProjektDokonceny();

	/**
	 * Vrátí stav: úkol aktivní
	 * @return
	 */
	public TaskState getUkolAktivni();

	/**
	 * Vrátí stav: úkol hotový
	 * @return
	 */
	public TaskState getUkolHotovy();

	/**
	 * Vrátí stav: úkol v kalendáři
	 * @return
	 */
	public TaskState getUkolVKalendari();

	/**
	 * Vrátí stav: úkol vytvořený
	 * @return
	 */
	public TaskState getUkolVytvoreny();

}