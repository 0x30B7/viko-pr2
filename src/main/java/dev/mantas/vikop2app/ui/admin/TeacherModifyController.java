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
import dev.mantas.vikop2app.model.helper.TeacherWithSubject;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class TeacherModifyController implements Initializable {

    private static final Subject UNLINK_SUBJECT = new Subject(-1, "-");

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
    private Teacher teacher;

    public TeacherModifyController() {
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
            List<Subject> allSubjects = subjectDao.getAllSubjects();
            List<TeacherWithSubject> teacherSubjectLinks = teacherSubjectLinkDao.getAllTeachersWithOptSubjects();
            return new Pair<>(allSubjects, teacherSubjectLinks);
        }).then((result) -> {
            List<Subject> subjectsWithoutATeacher = new ArrayList<>();
            List<TeacherWithSubject> teacherWithExistingSubjectList = result.getValue().stream()
                    .filter(link -> link.getSubject() != null).collect(Collectors.toList());

            top:
            for (Subject subject : result.getKey()) {
                for (TeacherWithSubject teacherWithSubject : teacherWithExistingSubjectList) {
                    if (subject.equals(teacherWithSubject.getSubject())) {
                        continue top;
                    }
                }

                subjectsWithoutATeacher.add(subject);
            }

            List<Subject> finalResult = new ArrayList<>(subjectsWithoutATeacher.size() + 1);
            finalResult.add(UNLINK_SUBJECT);
            finalResult.addAll(subjectsWithoutATeacher);

            validSubjects.setAll(finalResult);
        }).execute();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        fieldUserName.setText(capitalize(admin.getName()) + " " + capitalize(admin.getLastName()));
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;

        inputId.setText(teacher.getId() + "");
        inputId.setDisable(true);
        inputName.setText(capitalize(teacher.getName()));
        inputLastName.setText(capitalize(teacher.getLastName()));
        inputEmail.setText(teacher.getEmail());
        inputRoom.setText(teacher.getRoomNumber() + "");

        AsyncTask.async(() -> {
            return teacherSubjectLinkDao.getSubjectByTeacherId(teacher.getId());
        }).then((subject) -> {
            if (subject != null) {
                inputSubject.getSelectionModel().select(subject);
            }
        }).execute();

        updateTeacherStatus();
    }

    public void onConfirm(ActionEvent event) throws Exception {
        String name = inputName.getText();
        String lastName = inputLastName.getText();
        LecturerStatus status = inputStatus.getValue();
        String email = inputEmail.textProperty().isEmpty().get() ? null : inputEmail.getText();
        Integer roomNo = inputRoom.textProperty().isEmpty().get() ? null : Integer.parseInt(inputRoom.getText());
        Subject subject = inputSubject.getValue();

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            Teacher exist = teacherDao.getById(teacher.getId());
            if (exist == null) {
                return false;
            }

            if (subject != null) {
                teacherSubjectLinkDao.detachSubjectFromTeacher(teacher.getId());

                if (subject != UNLINK_SUBJECT) {
                    teacherSubjectLinkDao.attachSubjectToTeacher(teacher.getId(), subject.getId());
                }
            }

            teacherDao.updateTeacher(teacher.getId(), name.toLowerCase(Locale.ROOT), lastName.toLowerCase(Locale.ROOT),
                    status, email == null ? null : email.toLowerCase(Locale.ROOT), roomNo);
            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(AlertType.ERROR, "Nepavyko atnaujinti dėstytojo duomenų.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.ERROR, "Nepavyko atnaujinti dėstytojo duomenų.");
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

    // ======================================================================================================== //

    private void updateTeacherStatus() {
        if (!validStatuses.isEmpty() && inputStatus.getSelectionModel().isEmpty()) {
            if (teacher == null) {
                inputStatus.getSelectionModel().select(validStatuses.get(0));
            } else {
                for (LecturerStatus validStatus : validStatuses) {
                    if (validStatus.getId() == teacher.getLecturerStatus().getId()) {
                        inputStatus.getSelectionModel().select(validStatus);
                        return;
                    }
                }
            }
        }
    }

}
