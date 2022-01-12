package dev.mantas.vikop2app.ui.teacher;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.Constants;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class StudentGroupGradeModifyController implements Initializable {

    @FXML
    private Label fieldUserName;
    @FXML
    private Label fieldOccupation;

    @FXML
    private Label btnGradeName;
    @FXML
    private Label btnGroupName;

    @FXML
    private TableView<StudentAssignedGradeTableEntry> tableStudentGrades;
    private ObservableList<StudentAssignedGradeTableEntry> validStudentGradeList = FXCollections.observableArrayList();

    private final StudentGroupStudentLinkDao studentGroupStudentLinkDao;
    private final AssignedGradeDao assignedGradeDao;

    private Teacher teacher;
    private Subject subject;
    private StudentGroup studentGroup;
    private GradeWithSubject grade;

    public StudentGroupGradeModifyController() {
        ServiceManager manager = ServiceManager.getInstance();
        studentGroupStudentLinkDao = manager.getDAO(StudentGroupStudentLinkDao.class);
        assignedGradeDao = manager.getDAO(AssignedGradeDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapTable(tableStudentGrades, 0, "studentName");
        mapTable(tableStudentGrades, 1, "studentLastName");

        TableColumn columnGrade = new TableColumn<>("Įvertinimas");
        Callback<TableColumn<ComboBox<Integer>, Void>, TableCell<ComboBox<Integer>, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<ComboBox<Integer>, Void> call(final TableColumn<ComboBox<Integer>, Void> param) {
                        return new TableCell<>() {
                            ComboBox<Integer> gradeBox = new ComboBox<>();

                            {
                                gradeBox.setItems(FXCollections.observableList(IntStream.rangeClosed(0, 11)
                                        .boxed().sorted(Comparator.reverseOrder()).collect(Collectors.toList())));

                                gradeBox.setConverter(new StringConverter<>() {
                                    @Override
                                    public String toString(Integer integer) {
                                        return integer == null ? null : integer.equals(Constants.UNASSIGNED_GRADE_VALUE) ?
                                                "n" : integer.toString();
                                    }

                                    @Override
                                    public Integer fromString(String s) {
                                        return null;
                                    }
                                });

                                gradeBox.setOnAction(new EventHandler<>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        StudentAssignedGradeTableEntry data = tableStudentGrades.getItems().get(getIndex());
                                        Integer gradeValue = gradeBox.getSelectionModel().getSelectedItem();

                                        if (gradeValue.equals(data.gradeValue))
                                            return;

                                        AsyncTask.async(() -> {
                                            if (gradeValue.equals(Constants.UNASSIGNED_GRADE_VALUE)) {
                                                assignedGradeDao.unassignGrade(grade.getGrade().getId(), data.getStudent().getId());
                                            } else {
                                                assignedGradeDao.assignGrade(
                                                        grade.getGrade().getId(),
                                                        Date.from(Instant.now()),
                                                        data.getStudent().getId(),
                                                        gradeValue);
                                            }
                                            data.setGradeValue(gradeValue);
                                        }).exceptionally((ex) -> {
                                            ex.printStackTrace();
                                            gradeBox.getSelectionModel().select(data.getGradeValue());
                                            AlertUtil.alert(Alert.AlertType.ERROR, "Nepavyko atnaujinti studento pažymio.");
                                        }).execute();
                                    }
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    StudentAssignedGradeTableEntry data = tableStudentGrades.getItems().get(getIndex());
                                    gradeBox.getSelectionModel().select(data.getGradeValue());
                                    setGraphic(gradeBox);
                                }
                            }
                        };
                    }
                };

        int totalWidth = 0;
        for (TableColumn<?, ?> column : tableStudentGrades.getColumns()) {
            totalWidth += column.getPrefWidth();
        }

        columnGrade.setPrefWidth((tableStudentGrades.getPrefWidth() - totalWidth) - 1);
        columnGrade.setCellFactory(cellFactory);
        tableStudentGrades.getColumns().add(columnGrade);

        tableStudentGrades.setItems(validStudentGradeList);
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        fieldUserName.setText(capitalize(teacher.getLecturerStatus().getAbbreviation()) + " " +
                capitalize(teacher.getName()) + " " + capitalize(teacher.getLastName()));
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        fieldOccupation.setText(fieldOccupation.getText() + " • " + subject.getTitle());
    }

    public void setStudentGroup(StudentGroup group) {
        this.studentGroup = group;
        btnGroupName.setText(group.getTitle());
    }

    public void setGrade(GradeWithSubject grade) {
        this.grade = grade;
        btnGradeName.setText(grade.getGrade().getTitle());
    }

    public void setAssignedGrades(List<AssignedGrade> grades) {
        AsyncTask.async(() -> studentGroupStudentLinkDao.getStudentsByStudentGroupId(studentGroup.getId()))
                .then((studentsInGroup) -> {
                    List<AssignedGrade> gradesList = new ArrayList<>(grades);
                    for (Student student : studentsInGroup) {
                        boolean found = false;
                        for (int i = 0; i < gradesList.size(); i++) {
                            AssignedGrade assignedGrade = gradesList.get(i);
                            if (assignedGrade.getStudentId() == student.getId()) {
                                found = true;
                                validStudentGradeList.add(new StudentAssignedGradeTableEntry(assignedGrade, student));
                                gradesList.remove(i);
                                break;
                            }
                        }

                        if (!found) {
                            validStudentGradeList.add(new StudentAssignedGradeTableEntry(null, student));
                        }
                    }

                    tableStudentGrades.setItems(validStudentGradeList);
                }).execute();
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void onBackHome(MouseEvent event) throws Exception {
        SceneUtil.load(event, "teacher/home.fxml", TeacherController.class, (controller) -> {
            controller.setTeacher(teacher);
            controller.setSubject(subject);
        });
    }

    public void onBackGrades(MouseEvent event) throws Exception {
        SceneUtil.load(event, "teacher/grade-modify.fxml", GradeModifyController.class, (controller) -> {
            controller.setTeacher(teacher);
            controller.setSubject(subject);
            controller.setGrade(grade);
        });
    }

    // ======================================================================================================== //

    private void mapTable(TableView<?> view, int columnIndex, String fieldName) {
        TableColumn<?, ?> colStatus = view.getColumns().get(columnIndex);
        colStatus.setCellValueFactory(new PropertyValueFactory<>(fieldName));
    }

    // ======================================================================================================== //

    public static class StudentAssignedGradeTableEntry {

        private AssignedGrade assignedGrade;
        private Student student;
        private String studentName;
        private String studentLastName;
        private Integer gradeValue;

        public StudentAssignedGradeTableEntry(AssignedGrade assignedGrade, Student student) {
            this.student = student;
            this.gradeValue = assignedGrade == null ? Constants.UNASSIGNED_GRADE_VALUE : assignedGrade.getValue();
            this.studentName = capitalize(student.getName());
            this.studentLastName = capitalize(student.getLastName());
        }

        public AssignedGrade getAssignedGrade() {
            return assignedGrade;
        }

        public void setAssignedGrade(AssignedGrade assignedGrade) {
            this.assignedGrade = assignedGrade;
        }

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentLastName() {
            return studentLastName;
        }

        public void setStudentLastName(String studentLastName) {
            this.studentLastName = studentLastName;
        }

        public Integer getGradeValue() {
            return gradeValue;
        }

        public void setGradeValue(Integer gradeValue) {
            this.gradeValue = gradeValue;
        }

    }

}
