package application;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/** Clasa DAO (data access object) pentru gestionarea
 * operatiilor asupra bazei de date.
 */
public class ActivityDAO {
	/**
     * Creeaza tabela activities in baza de date daca nu exista.
     */
    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS activities (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                deadline TEXT NOT NULL,
                status TEXT DEFAULT 'In desfasurare',
                notified INTEGER DEFAULT 0
            )
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adauga o activitate noua in baza de date.
     *
     * @param activity activitatea de adaugat
     */
    public static void addActivity(Activity activity) {
        String sql = "INSERT INTO activities(name, category, deadline, status, notified) VALUES(?,?,?,?,?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, activity.getName());
            pstmt.setString(2, activity.getCategory());
            pstmt.setString(3, activity.getDeadline().toString());
            pstmt.setString(4, activity.getStatus());
            pstmt.setInt(5, activity.isNotified() ? 1 : 0);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Returneaza toate activitatile salvate in baza de date.
     *
     * @return lista activitatilor
     */
    public static List<Activity> getAllActivities() {
        List<Activity> list = new ArrayList<>();
        String sql = "SELECT * FROM activities";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Activity a = new Activity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        LocalDateTime.parse(rs.getString("deadline")),
                        rs.getString("status"),
                        rs.getInt("notified")==1
                );
                
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * Actualizeaza o activitate existenta.
     *
     * @param activity activitatea cu datele actualizate
     */
    public static void updateActivity(Activity activity) {
        String sql = "UPDATE activities SET name=?, category=?, deadline=?, status=?, notified=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, activity.getName());
            pstmt.setString(2, activity.getCategory());
            pstmt.setString(3, activity.getDeadline().toString());
            pstmt.setString(4, activity.getStatus());
            pstmt.setInt(5, activity.isNotified() ? 1 : 0);
            pstmt.setInt(6, activity.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * sterge o activitate din baza de date.
     *
     * @param id identificatorul activitatii
     */
    public static void deleteActivity(int id) {
        String sql = "DELETE FROM activities WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
