package dev.mantas.vikop2app.ui.admin;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.model.Admin;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.ResourceBundle;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class SubjectModifyController implements Initializable {

    @FXML
    private Label fieldUserName;

    @FXML
    private TextField inputTitle;

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;

    private final SubjectDao subjectDao;

    private Admin admin;
    private Subject subject;

    public SubjectModifyController() {
        ServiceManager manager = ServiceManager.getInstance();
        subjectDao = manager.getDAO(SubjectDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        fieldUserName.setText(capitalize(admin.getName()) + " " + capitalize(admin.getLastName()));
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        inputTitle.setText(subject.getTitle());
    }

    public void onConfirm(ActionEvent event) throws Exception {
        Date now = Date.from(Instant.now());
        String title = inputTitle.getText();

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            if (!title.equalsIgnoreCase(subject.getTitle())) {
                Subject existByName = subjectDao.getSubjectByTitle(title);
                if (existByName != null) {
                    return false;
                }
            }

            Subject exists = subjectDao.getById(subject.getId());
            if (exists == null) {
                return false;
            }

            subjectDao.updateSubject(subject.getId(), title);
            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko atnaujinti dalyko duomenų:",
                        "Dalykas su tokiu pavadinimu egzistuoja arba modifikuojamas dalykas nebeegzistuoja.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko atnaujinti dalyko duomenų.");
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
