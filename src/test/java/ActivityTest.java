package test.java;
import org.junit.jupiter.api.*;
import application.Activity;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    @Test
    void testFullConstructor() {
        LocalDateTime deadline = LocalDateTime.of(2026, 6, 15, 14, 30);
        Activity a = new Activity(42, "Examen", "Facultate", deadline, "In desfasurare", true);

        assertEquals(42, a.getId());
        assertEquals("Examen", a.getName());
        assertEquals("Facultate", a.getCategory());
        assertEquals(deadline, a.getDeadline());
        assertEquals("In desfasurare", a.getStatus());
        assertTrue(a.isNotified());
    }

    @Test
    void testSimpleConstructor() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(3);
        Activity a = new Activity("Raport", "Serviciu", deadline);

        assertEquals(-1, a.getId());
        assertEquals("Raport", a.getName());
        assertEquals("Serviciu", a.getCategory());
        assertEquals(deadline, a.getDeadline());
        assertEquals("In desfasurare", a.getStatus());
        assertFalse(a.isNotified());
    }

    @Test
    void testSetters() {
        Activity a = new Activity("Meeting", "Munca", LocalDateTime.now().plusHours(2));

        a.setName("Meeting");
        a.setCategory("Urgent");
        a.setStatus("Terminat");
        a.setNotified(true);

        assertEquals("Meeting", a.getName());
        assertEquals("Urgent", a.getCategory());
        assertEquals("Terminat", a.getStatus());
        assertTrue(a.isNotified());
    }
}
