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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class StudentGroupModifyController implements Initializable {

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
    private StudentGroup studentGroup;
    private List<Subject> studentGroupSubjectLinks;

    public StudentGroupModifyController() {
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
            updateStudentGroupSubjects();
        }).execute();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        fieldUserName.setText(capitalize(admin.getName()) + " " + capitalize(admin.getLastName()));
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
        inputTitle.setText(studentGroup.getTitle());

        AsyncTask.async(() -> {
            return studentGroupSubjectLinkDao.getSubjectsByStudentGroupId(studentGroup.getId());
        }).then((subjects) -> {
            studentGroupSubjectLinks = subjects;
            updateStudentGroupSubjects();
        }).execute();
    }

    public void onConfirm(ActionEvent event) throws Exception {
        String title = inputTitle.getText();
        List<Integer> selectedSubjects = inputSubjectListView.getItems().stream()
                .filter(ToggleButton::isSelected)
                .map(entry -> entry.getSubject().getId())
                .collect(Collectors.toList());

        btnSubmit.setDisable(true);
        btnCancel.setDisable(true);

        AsyncTask.async(() -> {
            if (!title.equalsIgnoreCase(studentGroup.getTitle())) {
                StudentGroup exist = studentGroupDao.getByTitle(title);
                if (exist != null) {
                    return false;
                }
            }

            StudentGroup exist = studentGroupDao.getById(studentGroup.getId());
            if (exist == null) {
                return false;
            }

            studentGroupDao.updateStudentGroup(studentGroup.getId(), title);

            studentGroupSubjectLinkDao.detachAllSubjectsFromStudentGroup(studentGroup.getId());
            if (!selectedSubjects.isEmpty()) {
                studentGroupSubjectLinkDao.attachSubjectsToStudentGroup(selectedSubjects, studentGroup.getId());
            }
            return true;
        }).then((success) -> {
            if (!success) {
                AlertUtil.alert(AlertType.ERROR, "Nepavyko atnaujinti studentų grupės duomenų: " +
                        "Studentų grupė su tokiu pavadinimu egzistuoja arba modifikuojama studentų grupė nebeegzistuoja.");
                return;
            }

            SceneUtil.load(event, "admin/home.fxml", AdminController.class, (controller) -> {
                controller.setAdmin(admin);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.ERROR, "Nepavyko atnaujinti studentų grupės duomenų.");
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

        private final Subject subject;

        public SubjectRadioButton(Subject subject) {
            super(subject.getTitle());
            this.subject = subject;
        }

        public Subject getSubject() {
            return subject;
        }

    }

    private void updateStudentGroupSubjects() {
        if (studentGroupSubjectLinks == null || studentGroupSubjectLinks.isEmpty()) return;
        if (!validSubjects.isEmpty() && validSubjects.stream().filter(ToggleButton::isSelected).findFirst().orElse(null) == null) {
            for (SubjectRadioButton subjectBtn : validSubjects) {
                for (Subject linkedSubject : studentGroupSubjectLinks) {
                    if (linkedSubject.getId() == subjectBtn.getSubject().getId()) {
                        subjectBtn.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

}
