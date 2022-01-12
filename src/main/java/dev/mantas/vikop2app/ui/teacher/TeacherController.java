package dev.mantas.vikop2app.ui.teacher;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;
import dev.mantas.vikop2app.ui.common.TableActionButtonFactory;
import dev.mantas.vikop2app.ui.common.TableActionButtonProvider;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class TeacherController implements Initializable {

    @FXML
    private Label fieldUserName;
    @FXML
    private Label fieldOccupation;

    @FXML
    private TableView<GradeTableEntry> tableGrades;
    private ObservableList<GradeTableEntry> validGradeList = FXCollections.observableArrayList();

    private GradeDao gradeDao;
    private AssignedGradeDao assignedGradeDao;

    private Teacher teacher;
    private Subject subject;

    public TeacherController() {
        ServiceManager manager = ServiceManager.getInstance();
        gradeDao = manager.getDAO(GradeDao.class);
        assignedGradeDao = manager.getDAO(AssignedGradeDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapTable(tableGrades, 0, "title");
        mapTable(tableGrades, 1, "createdAt");

        addColumnWithButtons(tableGrades,
                TableActionButtonFactory.createModifyButton(
                        GradeTableEntry.class, GradeWithSubject.class, GradeModifyController.class,
                        (data) -> gradeDao.getGradeWithSubject(data.getId()), validGradeList,
                        "teacher/grade-modify.fxml", (controller, grade) -> {
                            controller.setTeacher(teacher);
                            controller.setSubject(subject);
                            controller.setGrade(grade);
                        }),
                TableActionButtonFactory.createRemoveButton(
                        GradeTableEntry.class, GradeWithSubject.class,
                        (data) -> {
                            assignedGradeDao.unassignAllAssignedGrades(data.getId());
                            gradeDao.removeGrade(data.getId());
                            return true;
                        }, validGradeList,
                        "Ar tikrai norite šalinti bendra pažymį?"
                )
        );

        tableGrades.setItems(validGradeList);
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        fieldUserName.setText(capitalize(teacher.getLecturerStatus().getAbbreviation()) + " " +
                capitalize(teacher.getName()) + " " + capitalize(teacher.getLastName()));
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        fieldOccupation.setText(fieldOccupation.getText() + " • " + subject.getTitle());

        AsyncTask.async(() -> {
            return gradeDao.getGradesBySubject(subject.getId());
        }).then((entries) -> {
            validGradeList.setAll(entries.stream().map(o -> {
                return new GradeTableEntry(
                        o.getId(),
                        o.getTitle(),
                        o.getCreatedAt()
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

    public void onCreateGrade(ActionEvent event) throws Exception {
        SceneUtil.load(event, "teacher/grade-create.fxml", GradeCreateController.class, (controller) -> {
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

    public static class GradeTableEntry {

        private final int id;
        private String title;
        private Date createdAt;

        public GradeTableEntry(int id, String title, Date createdAt) {
            this.id = id;
            this.title = title;
            this.createdAt = createdAt;
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

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

    }

}
