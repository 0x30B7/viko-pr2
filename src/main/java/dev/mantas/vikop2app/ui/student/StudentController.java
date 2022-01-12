package dev.mantas.vikop2app.ui.student;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class StudentController implements Initializable {

    @FXML
    private Label fieldUserName;
    @FXML
    private Label fieldOccupation;

    @FXML
    private TableView<SubjectTableEntry> tableSubjects;
    private ObservableList<SubjectTableEntry> validSubjectList = FXCollections.observableArrayList();

    private SubjectDao subjectDao;
    private StudentGroupSubjectLinkDao studentGroupSubjectLinkDao;

    private Student student;
    private StudentGroup studentGroup;

    public StudentController() {
        ServiceManager manager = ServiceManager.getInstance();
        subjectDao = manager.getDAO(SubjectDao.class);
        studentGroupSubjectLinkDao = manager.getDAO(StudentGroupSubjectLinkDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapTable(tableSubjects, 0, "title");
//        mapTable(tableSubjects, 1, "gradeCount");
//        mapTable(tableSubjects, 2, "gradeAverage");

        tableSubjects.setItems(validSubjectList);
        tableSubjects.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SubjectTableEntry entry = tableSubjects.getSelectionModel().getSelectedItem();

                AsyncTask.async(() -> {
                    return subjectDao.getById(entry.getId());
                }).then((subject) -> {
                    if (subject == null) {
                        AlertUtil.alert(Alert.AlertType.ERROR,
                                "Pažymių peržiūra nepavyko:",
                                "Dalykas nebeegzistuoja, bandykite prisijungti iš naujo.");
                        return;
                    }

                    try {
                        SceneUtil.load(event, "student/subject-grades.fxml", SubjectGradesController.class, controller -> {
                            controller.setStudent(student);
                            controller.setStudentGroup(studentGroup);
                            controller.setSubject(subject);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertUtil.alert(Alert.AlertType.ERROR,
                                "Pažymių peržiūra nepavyko:",
                                "Įvyko vidinė klaida.");
                    }
                }).execute();
            }
        });


    }

    public void setStudent(Student student) {
        this.student = student;
        fieldUserName.setText(capitalize(student.getName()) + " " + capitalize(student.getLastName()));
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
        fieldOccupation.setText(fieldOccupation.getText() + " • " + studentGroup.getTitle());

        AsyncTask.async(() -> {
            return studentGroupSubjectLinkDao.getSubjectsByStudentGroupId(studentGroup.getId());
        }).then((entries) -> {
            validSubjectList.setAll(entries.stream().map(o -> {
                return new SubjectTableEntry(
                        o.getId(),
                        o.getTitle()
//                        , 0, 0F
                );
            }).collect(Collectors.toList()));
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            AlertUtil.alert(Alert.AlertType.WARNING, "Nepavyko užkrauti įvertinimų sąrašo.");
        }).execute();
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

    public static class SubjectTableEntry {

        private final int id;
        private String title;
//        private int gradeCount;
//        private float gradeAverage;

        public SubjectTableEntry(int id, String title/*, int gradeCount, float gradeAverage*/) {
            this.id = id;
            this.title = title;
//            this.gradeCount = gradeCount;
//            this.gradeAverage = gradeAverage;
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

//        public int getGradeCount() {
//            return gradeCount;
//        }
//
//        public void setGradeCount(int gradeCount) {
//            this.gradeCount = gradeCount;
//        }
//
//        public float getGradeAverage() {
//            return gradeAverage;
//        }
//
//        public void setGradeAverage(float gradeAverage) {
//            this.gradeAverage = gradeAverage;
//        }

    }

}
