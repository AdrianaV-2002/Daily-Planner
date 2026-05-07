package application;

import java.time.LocalDateTime;
/**
 * Reprezinta o activitate din programul zilnic.
 * O activitate are un nume, o categorie, data limita,
 * status si informatii despre notificare.
 */
public class Activity {
    private int id;
    private String name;
    private String category;
    private LocalDateTime deadline;
    private String status;
    private boolean notified;
    
    
    /**
     * Constructor complet pentru clasa Activity.
     *
     * @param id identificatorul activitatii
     * @param name numele activitatii
     * @param category categoria activitatii
     * @param deadline data si ora limita
     * @param status statusul activitatii
     * @param notified indica daca notificarea a fost trimisa
     */    
    public Activity(int id, String name, String category, LocalDateTime deadline, String status, boolean notified) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.deadline = deadline;
        this.status = status;
        this.notified = notified;
    }
    
    /**
     * Constructor pentru o activitate noua, fara ID.
     *
     * @param name numele activitatii
     * @param category categoria activitatii
     * @param deadline data si ora limita
     */

    public Activity(String name, String category, LocalDateTime deadline) {
        this(-1, name, category, deadline, "In desfasurare",false);
    }

    /** @return id-ul activitatii */
    public int getId() { return id; }
    /** @return numele activitatii */
    public String getName() { return name; }
    /** @param name seteaza numele activitatii */
    public void setName(String name) { this.name = name; }
    /** @return categoria activitatii */
    public String getCategory() { return category; }
    /**@param category seteaza categoria activitatii*/
    public void setCategory(String category) { this.category = category; }
    /** @return deadline-ul a activitatii */
    public LocalDateTime getDeadline() { return deadline; }
    /** @param deadline seteaza deadline-ul activitatii */
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    /** @return statusul activitatii */
    public String getStatus() { return status; }
    /** @param status seteaza statusul activitatii */
    public void setStatus(String status) { this.status = status; }
    /** @return true daca notificarea a fost trimisa */
    public boolean isNotified() { return notified; }
    /** @param notified seteaza starea notificarii */
    public void setNotified(boolean notified) { this.notified = notified; }
}
