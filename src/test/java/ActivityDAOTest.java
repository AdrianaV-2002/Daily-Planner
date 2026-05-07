package test.java;
import application.Activity;
import application.ActivityDAO;
import application.Database;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.*;

class ActivityDAOTest {

	@BeforeAll
	static void setup() {
	    Database.setTestDatabase();
	    ActivityDAO.createTable();
	}

    @BeforeEach
    void clearDatabase() {
        for (Activity a : ActivityDAO.getAllActivities()) {
            ActivityDAO.deleteActivity(a.getId());
        }
    }

    @Test
    void testAddActivity() {
        Activity a = new Activity("Test DAO", "JUnit",
                LocalDateTime.now().plusHours(2));

        ActivityDAO.addActivity(a);

        List<Activity> list = ActivityDAO.getAllActivities();
        assertEquals(1, list.size());
        assertEquals("Test DAO", list.get(0).getName());
    }

    @Test
    void testUpdateActivity() {
        Activity a = new Activity("Initial", "Cat",
                LocalDateTime.now().plusDays(1));
        ActivityDAO.addActivity(a);

        Activity saved = ActivityDAO.getAllActivities().get(0);
        saved.setName("Modificat");
        saved.setStatus("Terminat");

        ActivityDAO.updateActivity(saved);

        Activity updated = ActivityDAO.getAllActivities().get(0);
        assertEquals("Modificat", updated.getName());
        assertEquals("Terminat", updated.getStatus());
    }

    @Test
    void testDeleteActivity() {
        Activity a = new Activity("test", "Test",
                LocalDateTime.now().plusHours(5));
        ActivityDAO.addActivity(a);

        Activity saved = ActivityDAO.getAllActivities().get(0);
        ActivityDAO.deleteActivity(saved.getId());

        assertTrue(ActivityDAO.getAllActivities().isEmpty());
    }
}

