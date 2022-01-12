package dev.mantas.vikop2app.ui.admin;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.model.Admin;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class StudentGroupCreateController implements Initializable {

    @FXML
    private Label fieldUserName;

    @FXML
    private TextField inputTitle;
    @FXML
    private ListView<SubjectRadioButton> inputSubjectListView;
    private ObservableList<SubjectRadioButton> validSubjects = FXCollections.observableList(new ArrayList<>());

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;

    private final StudentGroupDao studentGroupDao;
    private final SubjectDao subjectDao;
    private final StudentGroupSubjectLinkDao studentGroupSubjectLinkDao;

    private Admin admin;

    public StudentGroupCreateController() {
        ServiceManager manager = ServiceManager.getInstance();
        studentGroupDao = manager.getDAO(StudentGroupDao.class);
        subjectDao = manager.getDAO(SubjectDao.class);
        studentGroupSubjectLinkDao = manager.getDAO(StudentGroupSubjectLinkDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputSubjectListView.setItems(validSubjects);

        AsyncTask.async(() -> {
            return subjectDao.getAllSubjects();
        }).then((subjects) -> {
            validSubjects.setAll(subjects.stream()
                    .map(SubjectRadioButton::new)
                    .collect(Collectors.toList()));
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
        String title = inputTitle.getText();
        List<Integer> selectedSubjects = inputSubjectListView.getItems().stream()
                .filter(ToggleButton::isSelected)
                .map(entry -> entry.getSubject().getId())
                .collect(Collectors.toList());

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            StudentGroup exist = studentGroupDao.getByTitle(title);
            if (exist != null) {
                return false;
            }

            studentGroupDao.createStudentGroup(title, now);

            if (!selectedSubjects.isEmpty()) {
                StudentGroup studentGroup = studentGroupDao.getByTitle(title);
                studentGroupSubjectLinkDao.attachSubjectsToStudentGroup(selectedSubjects, studentGroup.getId());
            }
            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(AlertType.ERROR, "Nepavyko sukurti naujos studentų grupės:",
                        "Studentų grupė su tokiu pavadinimu jau egzistuoja.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.ERROR, "Nepavyko sukurti naujos studentų grupės.");
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

    private static class SubjectRadioButton extends RadioButton {

        private Subject subject;

        public SubjectRadioButton(Subject subject) {
            super(subject.getTitle());
            this.subject = subject;
        }

        public Subject getSubject() {
            return subject;
        }

    }

}
