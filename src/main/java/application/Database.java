package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Gestioneaza conexiunea cu baza de date SQLite.
 */
public class Database {
	
    private static String URL = "jdbc:sqlite:database/activities.db";
    /**
     * Creeaza si returneaza o conexiune catre baza de date.
     *
     * @return conexiunea la baza de date
     * @throws SQLException daca apare o eroare de conectare
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    /**
     * Seteaza baza de date pentru rularea testelor unitare.
     */
    public static void setTestDatabase() {
        URL = "jdbc:sqlite:database/activities_test.db";
    }
}

