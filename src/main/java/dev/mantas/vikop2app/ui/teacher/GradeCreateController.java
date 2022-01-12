package dev.mantas.vikop2app.ui.teacher;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.ResourceBundle;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class GradeCreateController implements Initializable {

    @FXML
    private Label fieldUserName;

    @FXML
    private TextField inputTitle;

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;

    private final GradeDao gradeDao;

    private Teacher teacher;
    private Subject subject;

    public GradeCreateController() {
        ServiceManager manager = ServiceManager.getInstance();
        gradeDao = manager.getDAO(GradeDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        fieldUserName.setText(capitalize(teacher.getLecturerStatus().getAbbreviation()) + " " +
                capitalize(teacher.getName()) + " " + capitalize(teacher.getLastName()));
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void onConfirm(ActionEvent event) throws Exception {
        Date now = Date.from(Instant.now());
        String title = inputTitle.getText();

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            Grade exist = gradeDao.getGradeByTitleAndSubjectId(title, subject.getId());
            if (exist != null) {
                return false;
            }

            gradeDao.createGrade(now, title, subject.getId());
            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(AlertType.ERROR, "Nepavyko sukurti naujo pažymio:",
                        "Pažimys su tokiu pavadinimu jau egzistuoja.");
                return;
            }

            SceneUtil.load(event, "teacher/home.fxml", TeacherController.class, (controller) -> {
                controller.setTeacher(teacher);
                controller.setSubject(subject);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.ERROR, "Nepavyko sukurti naujo pažymio.");
        }).lastly(() -> {
            btnSubmit.setDisable(false);
            btnCancel.setDisable(false);
        }).execute();
    }

    public void onCancel(ActionEvent event) throws Exception {
        SceneUtil.load(event, "teacher/home.fxml", TeacherController.class, (controller) -> {
            controller.setTeacher(teacher);
            controller.setSubject(subject);
        });
    }

}
