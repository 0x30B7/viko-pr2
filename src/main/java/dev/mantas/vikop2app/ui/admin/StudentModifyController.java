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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class StudentModifyController implements Initializable {

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
    private Student student;

    public StudentModifyController() {
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

    public void setStudent(Student student) {
        this.student = student;

        inputId.setText(student.getId() + "");
        inputId.setDisable(true);
        inputName.setText(capitalize(student.getName()));
        inputLastName.setText(capitalize(student.getLastName()));

        AsyncTask.async(() -> {
            return studentGroupStudentLinkDao.getStudentGroupByStudentId(student.getId());
        }).then((studentGroup) -> {
            if (studentGroup != null && validStudentGroups.contains(studentGroup)) {
                inputStudentGroup.getSelectionModel().select(studentGroup);
            }
        }).execute();
    }

    public void onConfirm(ActionEvent event) throws Exception {
        String name = inputName.getText();
        String lastName = inputLastName.getText();
        StudentGroup studentGroup = inputStudentGroup.getValue();

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            Student exist = studentDao.getById(student.getId());
            if (exist == null) {
                return false;
            }

            if (studentGroup != null) {
                studentGroupStudentLinkDao.detachStudentFromStudentGroup(student.getId());
                studentGroupStudentLinkDao.attachStudentToStudentGroup(student.getId(), studentGroup.getId());
            }

            studentDao.updateStudent(student.getId(), name.toLowerCase(Locale.ROOT), lastName.toLowerCase(Locale.ROOT));
            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(AlertType.ERROR, "Nepavyko atnaujinti studento duomenų.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.ERROR, "Nepavyko atnaujinti studento duomenų.");
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
