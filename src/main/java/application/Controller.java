package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
/**
 * Controller JavaFX care gestioneaza interacțiunea
 * dintre interfata grafica si logica aplicatiei.
 */
public class Controller {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<LocalTime> timeSpinner;
    @FXML private TableView<Activity> activityTable;
    @FXML private TableColumn<Activity, String> categoryColumn;
    @FXML private TableColumn<Activity, String> nameColumn;
    @FXML private TableColumn<Activity, LocalDateTime> deadlineColumn;
    @FXML private TableColumn<Activity, String> statusColumn;

    @FXML private Spinner<Integer> notifyHoursSpinner;
    @FXML private Spinner<Integer> notifyMinutesSpinner;

    private ObservableList<Activity> activities;
    private Timeline notificationTimeline;
    
    

    @FXML
/**initializare tabel*/   
    public void initialize() {
        ActivityDAO.createTable();
        activities = FXCollections.observableArrayList(ActivityDAO.getAllActivities());
        activityTable.setItems(activities);

        //initializarea valorilor celulelor
        categoryColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        nameColumn.setCellValueFactory( cell -> new SimpleStringProperty(cell.getValue().getName()));
        deadlineColumn.setCellValueFactory( cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getDeadline()));
        statusColumn.setCellValueFactory(  cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        setupTimeSpinner();
        setupNotifySpinners();
       //activarea sortarii liniilor tabelului
        categoryColumn.setSortable(true);
        nameColumn.setSortable(true);

        // auto-umplerea campurilor odata cu selectarea liniei din tabel
        activityTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                categoryField.setText(selected.getCategory());
                datePicker.setValue(selected.getDeadline().toLocalDate());
                timeSpinner.getValueFactory().setValue(selected.getDeadline().toLocalTime());
            } else {
                resetFields();
            }                     
            
        });
        
//Sortarea implicita dupa data     
        activityTable.getSortOrder().clear();
        activityTable.getSortOrder().add(deadlineColumn);
        activityTable.sort();

        setupNotifications();
        
     // Formatter al afisarii datii in limba romana
        DateTimeFormatter niceFormatter = DateTimeFormatter
                .ofPattern("dd MMM yyyy, HH:mm")
                .withLocale(new Locale("ro", "RO"));

        deadlineColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(niceFormatter.format(item));
                }
            }
        });

        setupNotifications();
    }
/**setarea spinnerelor pentru deadline la valorile locale*/
    private void setupTimeSpinner() {
        var factory = new SpinnerValueFactory<LocalTime>() {
            {
                setValue(LocalTime.now().withMinute(0).withSecond(0).withNano(0));
            }
            @Override public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 15L));
            }
            @Override public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 15L));
            }
        };

        factory.setConverter(new StringConverter<>() {
            @Override public String toString(LocalTime time) {
                if (time == null) return "";
                return String.format("%02d:%02d", time.getHour(), time.getMinute());
            }

            @Override public LocalTime fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return LocalTime.now().withMinute(0).withSecond(0).withNano(0);
                }
                try {
                    return LocalTime.parse(string.trim());
                } catch (DateTimeParseException e) {
                    return factory.getValue(); // tine valoare valida
                }
            }
        });

        timeSpinner.setValueFactory(factory);
        timeSpinner.setEditable(true);
        timeSpinner.getEditor().setText(factory.getConverter().toString(factory.getValue()));
        
        
    }
/**setarea range-ului spinnerelor pentru ora si minute*/
    private void setupNotifySpinners() {
        notifyHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0));
        notifyMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 1));      
    }

    /**adaugare activitate*/
    @FXML
    public void addActivity() {
        Activity newActivity = getActivityFromFields();
        if (newActivity == null) return;

        ActivityDAO.addActivity(newActivity);
        refreshTable();
        resetFields();
    }

/**editarea activitatii selectate*/
    @FXML
    public void editActivity() {
        Activity selected = activityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selecteaza o activitate pentru a edita.");
            return;
        }

        Activity updated = getActivityFromFields();
        if (updated == null) return;
        
     // Verificam daca s-a schimbat deadline-ul
        boolean deadlineChanged = !selected.getDeadline().equals(updated.getDeadline());
        // aplicare modificari
        
        selected.setName(updated.getName());
        selected.setCategory(updated.getCategory());
        selected.setDeadline(updated.getDeadline());
        selected.setStatus("In desfasurare");
        
     // Resetam notificarea DOAR daca deadline-ul s-a schimbat
        if (deadlineChanged) {
            selected.setNotified(false);
        }

        ActivityDAO.updateActivity(selected);
        refreshTable();
        resetFields();
        showInfo("Activitate actualizata.");
    }

 /**stergere activitate*/
    @FXML
    public void deleteActivity() {
        Activity selected = activityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selecteaza o activitate pentru stergere.");
            return;
        }

        ActivityDAO.deleteActivity(selected.getId());
        refreshTable();
        resetFields();
        showInfo("Activitate stearsa.");
    }
 //marcarea ca terminat
    @FXML
    public void markAsCompleted() {
        Activity selected = activityTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selected.setStatus("Terminat");
        selected.setNotified(true);
        ActivityDAO.updateActivity(selected);
        refreshTable();
    }

    
    /**validare input si parsing*/
    private Activity getActivityFromFields() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();

        if (name.isBlank() || category.isBlank()) {
            showWarning("Numele si categoria sunt obligatorii.");
            return null;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            showWarning("Selecteaza data limita.");
            return null;
        }

        LocalTime time = timeSpinner.getValue();
        if (time == null) {
            showWarning("Selecteaza ora limita.");
            return null;
        }

        LocalDateTime deadline = LocalDateTime.of(date, time);

        if (deadline.isBefore(LocalDateTime.now())) {
            showWarning("Data limita nu poate fi in trecut.");
            return null;
        }

        return new Activity(name, category, deadline);
    }
/**functia de reimprospatare a afisarii tabelei dupa modificari*/
    private void refreshTable() {
        List<Activity> freshList = ActivityDAO.getAllActivities();
        activities.setAll(freshList);
    }
/**resetarea valorii campurilor de completare dupa efectuarea functiei de adaugare, stergere, modificare*/
    private void resetFields() {
        nameField.clear();
        categoryField.clear();
        datePicker.setValue(null);
        timeSpinner.getValueFactory().setValue(
                LocalTime.now().withMinute(0).withSecond(0).withNano(0));
    }

    /**notificari*/
    private void setupNotifications() {
        notificationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> checkForNotifications())
        );
        notificationTimeline.setCycleCount(Timeline.INDEFINITE);
        notificationTimeline.play();
    }
/**verificarea deadline-urilor pentru afisarea notificarilor*/
    private long getNotifyMinutesBefore() {
        return notifyHoursSpinner.getValue() * 60L + notifyMinutesSpinner.getValue();
    }
    
    private void checkForNotifications() {
    	LocalDateTime now = LocalDateTime.now();
        long notifyBeforeMin = getNotifyMinutesBefore();

        for (Activity activity : new ArrayList<>(activities)) {
            if (activity.getDeadline() == null) continue;

            var duration = java.time.Duration.between(now, activity.getDeadline());
            long minutesDiff = duration.toMinutes();

            // notificarea cand deadline-ul se apropie
            if (minutesDiff <= notifyBeforeMin && minutesDiff > -5 && !activity.isNotified()) {
                showNotification(activity, minutesDiff);
                activity.setNotified(true);
                ActivityDAO.updateActivity(activity);
            }
        }
    }
/**afisarea notificarilor*/
    private void showNotification(Activity activity, long minutesDiff) {
        String timeText = minutesDiff >= 0
                ? minutesDiff + " minute ramase"
                : "Intarziat cu " + (-minutesDiff) + " minute";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificare");
        alert.setHeaderText("Deadline apropiat / depasit!");
        alert.setContentText(
                "Activitate: " + activity.getName() + "\n" +
                "Categorie:  " + activity.getCategory() + "\n" +
                "Status:     " + timeText
        );
        alert.show();
    }

    /**alerte pentru completarea campurilor*/
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atentie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
/**informare efectuarea activitatii*/
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succes");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
