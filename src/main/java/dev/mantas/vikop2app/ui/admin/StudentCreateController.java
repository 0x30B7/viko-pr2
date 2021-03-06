package dev.mantas.vikop2app.ui.admin;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.model.Admin;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.ui.common.converter.StudentGroupStringConverter;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class StudentCreateController implements Initializable {

    @FXML
    private Label fieldUserName;

    @FXML
    private TextField inputId;
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputLastName;
    @FXML
    private ComboBox<StudentGroup> inputStudentGroup;
    private ObservableList<StudentGroup> validStudentGroups = FXCollections.observableList(new ArrayList<>());

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;

    private final StudentDao studentDao;
    private final StudentGroupDao studentGroupDao;
    private final StudentGroupStudentLinkDao studentGroupStudentLinkDao;

    private Admin admin;

    public StudentCreateController() {
        ServiceManager manager = ServiceManager.getInstance();
        studentDao = manager.getDAO(StudentDao.class);
        studentGroupDao = manager.getDAO(StudentGroupDao.class);
        studentGroupStudentLinkDao = manager.getDAO(StudentGroupStudentLinkDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputStudentGroup.setItems(validStudentGroups);
        inputStudentGroup.setConverter(new StudentGroupStringConverter());

        AsyncTask.async(() -> {
            return studentGroupDao.getAllStudentGroups();
        }).then((statuses) -> {
            validStudentGroups.setAll(statuses);
        }).execute();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        fieldUserName.setText(capitalize(admin.getName()) + " " + capitalize(admin.getLastName()));
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void onConfirm(ActionEvent event) throws Exception {
        Date now = Date.from(Instant.now());
        String rawId = inputId.getText();
        String name = inputName.getText();
        String lastName = inputLastName.getText();
        StudentGroup studentGroup = inputStudentGroup.getValue();

        Long id;

        // [1-6][YYMMDD][####]
//        if (rawId == null || rawId.length() != 11) {
//            AlertUtil.alert(Alert.AlertType.ERROR,
//                    "Nepavyko sukurti naujo studento",
//                    "??vestas neteisingas asmens kodas.");
//            return;
//        }
//
//        try {
//            id = Long.parseLong(rawId);
//
//            if (id < 1_000000_0000L) {
//                AlertUtil.alert(Alert.AlertType.ERROR,
//                        "Nepavyko sukurti naujo studento",
//                        "??vestas neteisingas asmens kodas.");
//            }
//        } catch (Exception ex) {
//            AlertUtil.alert(Alert.AlertType.ERROR,
//                    "Nepavyko sukurti naujo studento",
//                    "Asmens kodas turi b??ti teigiamas skai??ius.");
//            return;
//        }

        try {
            id = Long.parseLong(rawId);
            if (id < 0) {
                throw new Exception();
            }
        } catch (Exception ex) {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo studento:",
                    "Netinkamai ??vestas asmens kodo laukas");
            return;
        }

        if (name == null) {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo studento:",
                    "Netinkamai ??vestas vardo laukas");
            return;
        }

        if (lastName == null) {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo studento:",
                    "Netinkamai ??vestas pavard??s laukas");
            return;
        }

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            Student exist = studentDao.getById(id);
            if (exist != null) {
                return false;
            }

            studentDao.createStudent(id, now, name.toLowerCase(Locale.ROOT), lastName.toLowerCase(Locale.ROOT));
            if (studentGroup != null) {
                studentGroupStudentLinkDao.attachStudentToStudentGroup(id, studentGroup.getId());
            }

            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo studento:",
                        "Studentas su tokiu asmens kodu jau egzistuoja.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo studento.");
            ex.printStackTrace();
        }).lastly(() -> {
            btnSubmit.setDisable(false);
            btnCancel.setDisable(false);
        }).execute();
    }

    public void onCancel(ActionEvent event) throws Exception {
        SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
            controller.setAdmin(admin);
        });
    }

}
