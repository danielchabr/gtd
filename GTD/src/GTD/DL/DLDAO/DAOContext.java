package GTD.DL.DLDAO;

import GTD.DL.DLEntity.Context;
import GTD.DL.DLEntity.Person;
import GTD.DL.DLInterfaces.IDAOContext;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

/**
 * Trída zapouzdruje metody pro ukládání a nacítání kontextu z databáze.
 *
 * @author GTD team
 * @version 1.0
 */
public class DAOContext implements IDAOContext {

    /**
     * Kontruktor kontextu
     */
    public DAOContext() {

    }

    /**
     * Vytvorí nový kontext zadaných vlastností a uloží ho do databáze.
     *
     * @param kontext
     */
    public boolean createContext(Context kontext) {
        Connection con = DatabaseConnection.getConnection();
        try {
            String jobquery = "begin API.CONTEXTS_IU("
                    + "inp_name  => ? "
                    + "inp_id_person  => ? "
                    + "); end;";
            CallableStatement callStmt = con.prepareCall(jobquery);
            callStmt.setString(1, kontext.getKontextNazev());
            callStmt.setInt(2, DatabaseConnection.getID());
            callStmt.execute();
            callStmt.close();
        } catch (SQLException e) {
            DatabaseConnection.showError("DB query error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Smaže kontext z databáze.
     *
     * @param kontext
     */
    public boolean deleteContext(Context kontext) {
        Connection con = DatabaseConnection.getConnection();
        try {
            String jobquery = "begin API.CONTEXTS_DEL(inp_id  => ? ); end;";
            CallableStatement callStmt = con.prepareCall(jobquery);
            callStmt.setInt(1, kontext.getKontextId());
            callStmt.execute();
            callStmt.close();
        } catch (SQLException e) {
            DatabaseConnection.showError("DB query error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Vrátí všechny kontexty v systému.
     *
     * @return List<Kontext>
     */
    public List getAllContexts() {
        List<Context> kontexty = new ArrayList<Context>();
        Connection con = DatabaseConnection.getConnection();
        try {
            Statement stmt = con.createStatement();
            //Podminka pro prihlasenou osobu + DatabaseConnection.getID());
            ResultSet rset = stmt.executeQuery("select id, name from contexts");
            while (rset.next()) {
                Context kon = new Context(rset.getInt(1), rset.getString(2));
                //System.out.println(ukl);
                kontexty.add(kon);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            DatabaseConnection.showError("DB query error: " + e.getMessage());
        }
        return kontexty;
    }

    /**
     * Vrátí kontext podle jeho ID.
     *
     * @param id
     * @return kontext
     */
    public Context getContext(int id) {
        Context kontext = null;
        Connection con = DatabaseConnection.getConnection();
        try {
            String jobquery = "select id, name "
                    + "from contexts "
                    + "where id = ? ";// + id);
            PreparedStatement stmt = con.prepareStatement(jobquery);
            stmt.setInt(1, id);
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                kontext = new Context(rset.getInt(1), rset.getString(2));
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            DatabaseConnection.showError("DB query error: " + e.getMessage());
        }
        return kontext;
    }

    /**
     * Uloží zmenený kontext.
     *
     * @param kontext
     */
    public boolean updateContext(Context kontext) {
        Connection con = DatabaseConnection.getConnection();
        try {
            String jobquery = "begin API.CONTEXTS_IU("
                    + "inp_id  =>" + kontext.getKontextId()
                    + "inp_id_name  => '" + kontext.getKontextNazev() + "'"
                    + "inp_id_person  =>" + DatabaseConnection.getID()
                    + "); end;";
            CallableStatement callStmt = con.prepareCall(jobquery);
            callStmt.setInt(1, kontext.getKontextId());
            callStmt.setString(2, kontext.getKontextNazev());
            callStmt.setInt(2, DatabaseConnection.getID());
            callStmt.execute();
            callStmt.close();
        } catch (SQLException e) {
            DatabaseConnection.showError("DB query error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Vrátí všechny kontexty patrící zadané osobe.
     *
     * @param osoba
     * @return List<Kontext>
     */
    public List getContextsOfPerson(Person osoba) {
        List<Context> kontexty = new ArrayList<Context>();
        Connection con = DatabaseConnection.getConnection();
        try {
            String jobquery = "select id, name "
                    + "from contexts "
                    + "where id_person = ? ";
            PreparedStatement stmt = con.prepareStatement(jobquery);
            stmt.setInt(1, osoba.getId());
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                Context kon = new Context(rset.getInt(1), rset.getString(2));
                kontexty.add(kon);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            DatabaseConnection.showError("DB query error: " + e.getMessage());
        }
        return kontexty;
    }

}
