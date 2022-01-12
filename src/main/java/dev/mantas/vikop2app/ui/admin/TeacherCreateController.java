package dev.mantas.vikop2app.ui.admin;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.LecturerStatusDao;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.data.dao.type.TeacherDao;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.model.Admin;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.ui.common.converter.LecturerStatusStringConverter;
import dev.mantas.vikop2app.ui.common.converter.SubjectStringConverter;
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

public class TeacherCreateController implements Initializable {

    @FXML
    private Label fieldUserName;

    @FXML
    private TextField inputId;
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputLastName;
    @FXML
    private ComboBox<LecturerStatus> inputStatus;
    private ObservableList<LecturerStatus> validStatuses = FXCollections.observableList(new ArrayList<>());
    @FXML
    private TextField inputEmail;
    @FXML
    private TextField inputRoom;
    @FXML
    private ComboBox<Subject> inputSubject;
    private ObservableList<Subject> validSubjects = FXCollections.observableList(new ArrayList<>());

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;

    private final TeacherDao teacherDao;
    private final LecturerStatusDao lecturerStatusDao;
    private final SubjectDao subjectDao;
    private final TeacherSubjectLinkDao teacherSubjectLinkDao;

    private Admin admin;

    public TeacherCreateController() {
        ServiceManager manager = ServiceManager.getInstance();
        teacherDao = manager.getDAO(TeacherDao.class);
        lecturerStatusDao = manager.getDAO(LecturerStatusDao.class);
        subjectDao = manager.getDAO(SubjectDao.class);
        teacherSubjectLinkDao = manager.getDAO(TeacherSubjectLinkDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputStatus.setItems(validStatuses);
        inputStatus.setConverter(new LecturerStatusStringConverter());
        inputSubject.setItems(validSubjects);
        inputSubject.setConverter(new SubjectStringConverter());

        AsyncTask.async(() -> {
            return lecturerStatusDao.getStatues();
        }).then((statuses) -> {
            validStatuses.setAll(statuses);
            inputStatus.getSelectionModel().select(statuses.get(0));
        }).execute();

        AsyncTask.async(() -> {
            return subjectDao.getAllSubjects();
        }).then((statuses) -> {
            validSubjects.setAll(statuses);
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
        LecturerStatus status = inputStatus.getValue();
        String email = inputEmail.textProperty().isEmpty().get() ? null : inputEmail.getText();
        Integer roomNo = inputRoom.textProperty().isEmpty().get() ? null : Integer.parseInt(inputRoom.getText());
        Subject subject = inputSubject.getValue();

        Long id;

        // [1-6][YYMMDD][####]
//        if (rawId == null || rawId.length() != 11) {
//            AlertUtil.alert(Alert.AlertType.ERROR,
//                    "Nepavyko sukurti naujo dėstytojo",
//                    "Įvestas neteisingas asmens kodas.");
//            return;
//        }
//
//        try {
//            id = Long.parseLong(rawId);
//
//            if (id < 1_000000_0000L) {
//                AlertUtil.alert(Alert.AlertType.ERROR,
//                        "Nepavyko sukurti naujo dėstytojo",
//                        "Įvestas neteisingas asmens kodas.");
//            }
//        } catch (Exception ex) {
//            AlertUtil.alert(Alert.AlertType.ERROR,
//                    "Nepavyko sukurti naujo dėstytojo",
//                    "Asmens kodas turi būti teigiamas skaičius.");
//            return;
//        }

        try {
            id = Long.parseLong(rawId);
            if (id < 0) {
                throw new Exception();
            }
        } catch (Exception ex) {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo dėstytojo:",
                    "Netinkamai įvestas asmens kodo laukas");
            return;
        }

        if (name == null) {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo dėstytojo:",
                    "Netinkamai įvestas vardo laukas");
            return;
        }

        if (lastName == null) {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo dėstytojo:",
                    "Netinkamai įvestas pavardės laukas");
            return;
        }

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            Teacher exist = teacherDao.getById(id);
            if (exist != null) {
                return false;
            }

            teacherDao.createTeacher(id, now, name.toLowerCase(Locale.ROOT), lastName.toLowerCase(Locale.ROOT),
                    status, email == null ? null : email.toLowerCase(Locale.ROOT), roomNo);

            if (subject != null) {
                teacherSubjectLinkDao.attachSubjectToTeacher(id, subject.getId());
            }

            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo dėstytojo:",
                        "Dėstytojas su tokiu asmens kodu jau egzistuoja.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko sukurti naujo dėstytojo.");
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
