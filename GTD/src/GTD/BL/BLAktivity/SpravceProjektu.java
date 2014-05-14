package GTD.BL.BLAktivity;
import GTD.BL.BLOsoby.SpravceOsob;
import GTD.DL.DLDAO.DAOProjekt;
import GTD.DL.DLEntity.Cinnost;
import GTD.DL.DLEntity.Osoba;
import GTD.DL.DLEntity.Projekt;
import GTD.DL.DLInterfaces.IDAOProjekt;
import java.util.List;

/**
 * Trída pro manipulaci s projekty.
 * @version 1.0
 */
public class SpravceProjektu {

	private IDAOProjekt DAOProjekt;
	/**
	 * Odkaz na SpravceCinnosti - při přidání projektu vzniklého z činnosti je třeba
	 * tuto činnost označit jako "zpracovanou" - to zařídí právě SpravceCinnosti.
	 */
	private SpravceCinnosti spravceCinnosti;
	/**
	 * Správce osob - pomocí něj přistupují ostatní správci k přihlášenému uživateli.
	 */
	private SpravceOsob spravceOsob;

	/**
	 *
	 */
	public SpravceProjektu(){
		DAOProjekt = new DAOProjekt();
		spravceCinnosti = new SpravceCinnosti();
	}

	/**
	 * Vytvorí nový projekt zadaných vlastností a uloží ho do databáze. Podprojekt v
	 * projektu může vytvořit pouze vlastník tohoto nadřazeného projektu.
	 * 
	 * @param projekt
	 * @param cinnost    Činnost, ze které projekt vznikl (pokud existuje) - používá
	 * se pro označení činnosti jako "zpracované".
	 * @return 
	 */
	public boolean addProjekt(Projekt projekt, Cinnost cinnost){
		if (cinnost != null) {
			spravceCinnosti.deleteCinnost(cinnost);
		}
		return DAOProjekt.createProjekt(projekt);
	}

	/**
	 * Smaže projekt (resp. označí jako smazaný) z databáze spolu se všemi jeho úkoly
	 * a podprojekty. Projekt může mazat jeho vlastník nebo vlastník nadřazeného
	 * projektu (v 1.úrovni).
	 * 
	 * @param projekt
	 * @return 
	 */
	public boolean deleteProjekt(Projekt projekt){
		return DAOProjekt.deleteProjekt(projekt);
	}

	/**
	 * Vrátí projekt podle jeho ID.
	 * 
	 * @param id
	 * @return 
	 */
	public Projekt getProjekt(int id){
		return DAOProjekt.getProjekt(id);
	}

	/**
	 * Uloží změněný projekt (změna názvu/popisu). Měnit projekt může  jeho vlastník
	 * nebo vlastník nadřazeného projektu (v 1.úrovni).
	 * 
	 * @param projekt
	 * @return 
	 */
	public boolean updateProjekt(Projekt projekt){
		return DAOProjekt.updateProjekt(projekt);
	}

	/**
	 * Vrátí všechny projekty patrící zadané osobe.
	 * 
	 * @param osoba
	 * @return 
	 */
	public List getProjektyOsoby(Osoba osoba){
		return DAOProjekt.getProjektyOsoby(osoba);
	}

	/**
	 * Vrátí všechny projekty
	 * 
	 * @return 
	 */
	public List getAllProjekty(){
		return DAOProjekt.getAllProjekty();
	}

	/**
	 * Označí projekt jako "dokončený". Dokončit projekt může jeho vlastník nebo
	 * vlastník nadřazeného projektu (v 1.úrovni).
	 * 
	 * @param projekt
	 * @return 
	 */
	public boolean finishProjekt(Projekt projekt){
		return DAOProjekt.updateProjekt(projekt);
	}

}