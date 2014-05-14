package GTD.DL.DLDAO;

import GTD.PL.PLView.GTDGUI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton for holding an instance of database connection
 */
public class DatabaseConnection {

	private static DatabaseConnection instance = null;

	private Connection connection;
	private int id;
	private String username;

	private DatabaseConnection(String username, String password) throws SQLException {
		// pavlim33 oracleGTD
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your Oracle JDBC Driver?");
			//e.printStackTrace();
			return;
		}
		connection = DriverManager.getConnection(
			"jdbc:oracle:thin:@oracle.fit.cvut.cz:1521:ORACLE",
			username,
			password);
	}

	/**
	 *
	 * @return
	 */
	public static Connection getConnection() {
		if(instance == null) {
			return null;
		}
		return instance.connection;
	}

	/**
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean login(String username, String password) {
		if(instance == null) {
			try {
				instance = new DatabaseConnection(username, password);
			} catch (SQLException e) {
				System.out.println("Connection Failed! Check output console");
				//e.printStackTrace();
				instance = null;
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 */
	public static void closeConnection() {
		try {
			if(instance != null) {
				instance.connection.close();
				System.out.println("closing connection");
			}
		} catch (SQLException e) {
			System.err.println("closing connection unsuccessful");
		}
	}

	/**
	 *
	 * @return
	 */
	public static int getID() {
		return DatabaseConnection.instance.id;
	}

	/**
	 *
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 *
	 * @param error
	 */
	public static void showError(String error) {
		GTDGUI.getGTDGUI().showError(error);
		System.err.println(error);
	}
}
