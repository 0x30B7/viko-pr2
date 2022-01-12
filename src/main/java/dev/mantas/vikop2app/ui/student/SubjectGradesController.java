package dev.mantas.vikop2app.ui.student;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.model.Student;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class SubjectGradesController implements Initializable {

    @FXML
    private Label fieldUserName;
    @FXML
    private Label fieldOccupation;
    @FXML
    private Label btnSubjectName;
    @FXML
    private Label fieldTeacherName;
    @FXML
    private TextFlow fieldTeacherInfo;

    @FXML
    private TableView<GradeTableEntry> tableGrades;
    private ObservableList<GradeTableEntry> validGradeList = FXCollections.observableArrayList();

    private AssignedGradeDao assignedGradeDao;
    private TeacherSubjectLinkDao teacherSubjectLinkDao;

    private Student student;
    private StudentGroup studentGroup;
    private Subject subject;

    public SubjectGradesController() {
        ServiceManager manager = ServiceManager.getInstance();
        assignedGradeDao = manager.getDAO(AssignedGradeDao.class);
        teacherSubjectLinkDao = manager.getDAO(TeacherSubjectLinkDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapTable(tableGrades, 0, "title");
        mapTable(tableGrades, 1, "gradeValue");
        mapTable(tableGrades, 2, "gradeAssignDate");

        tableGrades.setItems(validGradeList);
    }

    public void setStudent(Student student) {
        this.student = student;
        fieldUserName.setText(capitalize(student.getName()) + " " + capitalize(student.getLastName()));
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
        fieldOccupation.setText(fieldOccupation.getText() + " • " + studentGroup.getTitle());

        AsyncTask.async(() -> {
            return assignedGradeDao.getAssignedGradesForStudentAndSubject(student.getId(), subject.getId());
        }).then((entries) -> {
            validGradeList.setAll(entries.stream().map(o -> {
                return new GradeTableEntry(
                        o.getGrade().getId(),
                        o.getGrade().getTitle(),
                        o.hasAssignedGrade() ? o.getAssignedGrade().getValue() + "" : "n",
                        o.hasAssignedGrade() ? o.getAssignedGrade().getCreatedAt() : null
                );
            }).collect(Collectors.toList()));
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            AlertUtil.alert(Alert.AlertType.WARNING, "Nepavyko užkrauti įvertinimų sąrašo.");
        }).execute();
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        btnSubjectName.setText(subject.getTitle());

        AsyncTask.async(() -> {
            return teacherSubjectLinkDao.getTeacherBySubjectId(subject.getId());
        }).then((teacher) -> {
            if (teacher == null) {
                fieldTeacherName.setVisible(false);
                fieldTeacherInfo.setVisible(false);
                return;
            }

            fieldTeacherName.setText(teacher.getLecturerStatus().getAbbreviation() + " "
                    + capitalize(teacher.getName()) + " " + capitalize(teacher.getLastName()));

            if (teacher.getEmail() != null) {
                Text key = new Text("El. paštas: ");
                Text value = new Text(teacher.getEmail());
                value.setStyle("-fx-font-weight: bold");

                fieldTeacherInfo.getChildren().addAll(key, value);
            }

            if (teacher.getRoomNumber() != 0) {
                if (teacher.getEmail() != null) { // last component
                    Text separator = new Text(" • ");
                    fieldTeacherInfo.getChildren().add(separator);
                }

                Text key = new Text("Kabinetas: ");
                Text value = new Text(teacher.getRoomNumber() + "");
                value.setStyle("-fx-font-weight: bold");

                fieldTeacherInfo.getChildren().addAll(key, value);
            }
        }).execute();
    }

    public void onBackHome(MouseEvent event) throws Exception {
        SceneUtil.load(event, "student/home.fxml", StudentController.class, controller -> {
            controller.setStudent(student);
            controller.setStudentGroup(studentGroup);
        });
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    // ======================================================================================================== //

    private void mapTable(TableView<?> view, int columnIndex, String fieldName) {
        TableColumn<?, ?> colStatus = view.getColumns().get(columnIndex);
        colStatus.setCellValueFactory(new PropertyValueFactory<>(fieldName));
    }

    // ======================================================================================================== //

    public static class GradeTableEntry {

        private final int id;
        private String title;
        private String gradeValue;
        private String gradeAssignDate;

        public GradeTableEntry(int id, String title, String gradeValue, Date gradeAssignDate) {
            this.id = id;
            this.title = title;
            this.gradeValue = gradeValue;
            this.gradeAssignDate = gradeAssignDate == null ? "-" : gradeAssignDate.toString();
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGradeValue() {
            return gradeValue;
        }

        public void setGradeValue(String gradeValue) {
            this.gradeValue = gradeValue;
        }

        public String getGradeAssignDate() {
            return gradeAssignDate;
        }

        public void setGradeAssignDate(String gradeAssignDate) {
            this.gradeAssignDate = gradeAssignDate;
        }

    }

}
