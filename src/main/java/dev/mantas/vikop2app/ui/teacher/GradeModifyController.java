package dev.mantas.vikop2app.ui.teacher;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;
import dev.mantas.vikop2app.ui.common.TableActionButtonProvider;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Pair;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class GradeModifyController implements Initializable {

    @FXML
    private Label fieldUserName;
    @FXML
    private Label fieldOccupation;

    @FXML
    private Label btnGradeName;
    @FXML
    private TableView<GradeStudentGroupTableEntry> tableStudentGroups;
    private ObservableList<GradeStudentGroupTableEntry> validStudentGroupList = FXCollections.observableArrayList();

    private final StudentGroupSubjectLinkDao studentGroupSubjectLinkDao;
    private final StudentGroupStudentLinkDao studentGroupStudentLinkDao;
    private final AssignedGradeDao assignedGradeDao;

    private Teacher teacher;
    private Subject subject;
    private GradeWithSubject grade;

    public GradeModifyController() {
        ServiceManager manager = ServiceManager.getInstance();
        studentGroupSubjectLinkDao = manager.getDAO(StudentGroupSubjectLinkDao.class);
        studentGroupStudentLinkDao = manager.getDAO(StudentGroupStudentLinkDao.class);
        assignedGradeDao = manager.getDAO(AssignedGradeDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapTable(tableStudentGroups, 0, "studentGroupTitle");
        mapTable(tableStudentGroups, 1, "gradedStudentCount");
        mapTable(tableStudentGroups, 2, "totalStudentCount");

        addColumnWithButtons(tableStudentGroups, new TableActionButtonProvider<GradeStudentGroupTableEntry>() {
            @Override
            public Button createButton(int index, Supplier<GradeStudentGroupTableEntry> dataProvider, Supplier<Button[]> btnContext) {
                Button btn = new Button("Modifikuoti");
                btn.setOnAction((e) -> {
                    GradeStudentGroupTableEntry data = dataProvider.get();
                    Button[] buttons = btnContext.get();

                    for (Button button : buttons) {
                        button.setDisable(true);
                    }

                    AsyncTask.async(() -> assignedGradeDao.getAssignedGrades(grade.getGrade().getId(), data.getStudentGroup().getId()))
                            .then((validResult) -> {
                                SceneUtil.load(e, "teacher/student-group-grade-modify.fxml", StudentGroupGradeModifyController.class, (controller) -> {
                                    controller.setTeacher(teacher);
                                    controller.setSubject(subject);
                                    controller.setStudentGroup(data.getStudentGroup());
                                    controller.setGrade(grade);
                                    controller.setAssignedGrades(validResult);
                                });
                            }).lastly(() -> {
                                for (Button button : buttons) {
                                    button.setDisable(false);
                                }
                            }).execute();
                });
                return btn;
            }
        });

        tableStudentGroups.setItems(validStudentGroupList);
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

    public void setGrade(GradeWithSubject grade) {
        this.grade = grade;
        btnGradeName.setText(grade.getGrade().getTitle());

        AsyncTask.async(() -> studentGroupSubjectLinkDao.getStudentGroupsBySubjectId(subject.getId())).then((groups) -> {
            AsyncTask.async(() -> {
                List<Integer> associatedStudentGroupIds = groups.stream().map(StudentGroup::getId).collect(Collectors.toList());
                Map<Integer, Integer> groupAssignedGradeCount = assignedGradeDao.getAssignedGradeCountInStudentGroupsById(grade.getGrade().getId(), associatedStudentGroupIds);
                Map<Integer, Integer> groupStudentCounts = studentGroupStudentLinkDao.getStudentCountInStudentGroupsByIds(associatedStudentGroupIds);
                return new Pair<>(groupAssignedGradeCount, groupStudentCounts);
            }).then((data) -> {
                Map<Integer, Integer> groupAssignedGradeCount = data.getKey();
                Map<Integer, Integer> groupStudentCounts = data.getValue();

                validStudentGroupList.setAll(groups.stream().map(group -> {
                    return new GradeStudentGroupTableEntry(group,
                            groupAssignedGradeCount.getOrDefault(group.getId(), 0),
                            groupStudentCounts.getOrDefault(group.getId(), 0));
                }).collect(Collectors.toList()));
            }).execute();
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

    // ======================================================================================================== //

    private void mapTable(TableView<?> view, int columnIndex, String fieldName) {
        TableColumn<?, ?> colStatus = view.getColumns().get(columnIndex);
        colStatus.setCellValueFactory(new PropertyValueFactory<>(fieldName));
    }

    private <T> void addColumnWithButtons(TableView<T> view, TableActionButtonProvider<T>... buttons) {
        TableColumn<T, Void> columnActions = new TableColumn<>("Veiksmai");
        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                        return new TableCell<>() {
                            Button[] activeButtons = null;

                            {
                                Supplier<Button[]> activeButtonContext = () -> activeButtons;
                                activeButtons = Arrays.stream(buttons)
                                        .map(o -> o.createButton(
                                                getIndex(),
                                                () -> getTableView().getItems().get(getIndex()),
                                                activeButtonContext
                                        ))
                                        .toArray(Button[]::new);
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox box = new HBox(activeButtons);
                                    box.setSpacing(5);
                                    setGraphic(box);
                                }
                            }
                        };
                    }
                };

        int totalWidth = 0;
        for (TableColumn<?, ?> column : view.getColumns()) {
            totalWidth += column.getPrefWidth();
        }

        columnActions.setPrefWidth((view.getPrefWidth() - totalWidth) - 1);
        columnActions.setCellFactory(cellFactory);
        view.getColumns().add(columnActions);
    }

    // ======================================================================================================== //

    public static class GradeStudentGroupTableEntry {

        private StudentGroup studentGroup;
        private String studentGroupTitle;
        private int gradedStudentCount;
        private int totalStudentCount;

        public GradeStudentGroupTableEntry(StudentGroup studentGroup, int gradedStudentCount, int totalStudentCount) {
            this.studentGroup = studentGroup;
            this.studentGroupTitle = capitalize(studentGroup.getTitle());
            this.gradedStudentCount = gradedStudentCount;
            this.totalStudentCount = totalStudentCount;
        }

        public StudentGroup getStudentGroup() {
            return studentGroup;
        }

        public void setStudentGroup(StudentGroup studentGroup) {
            this.studentGroup = studentGroup;
        }

        public String getStudentGroupTitle() {
            return studentGroupTitle;
        }

        public void setStudentGroupTitle(String studentGroupTitle) {
            this.studentGroupTitle = studentGroupTitle;
        }

        public int getGradedStudentCount() {
            return gradedStudentCount;
        }

        public void setGradedStudentCount(int gradedStudentCount) {
            this.gradedStudentCount = gradedStudentCount;
        }

        public int getTotalStudentCount() {
            return totalStudentCount;
        }

        public void setTotalStudentCount(int totalStudentCount) {
            this.totalStudentCount = totalStudentCount;
        }

    }

}
