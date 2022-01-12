package dev.mantas.vikop2app.ui.admin;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.data.dao.type.TeacherDao;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.model.Admin;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class AdminController implements Initializable {

    private static int LAST_ACTIVE_TAB_VIEW = 0;

    private boolean initialized;
    @FXML
    private Label fieldUserName;

    @FXML
    private TabPane table;

    @FXML
    private Tab tabTeachers;
    @FXML
    private TableView<TeacherTableEntry> tableTeachers;
    private ObservableList<TeacherTableEntry> validTeacherList = FXCollections.observableArrayList();
    @FXML
    private Tab tabSubjects;
    @FXML
    private TableView<SubjectTableEntry> tableSubjects;
    private ObservableList<SubjectTableEntry> validSubjectList = FXCollections.observableArrayList();
    @FXML
    private Tab tabStudents;
    @FXML
    private TableView<StudentTableEntry> tableStudents;
    private ObservableList<StudentTableEntry> validStudentsList = FXCollections.observableArrayList();
    @FXML
    private Tab tabStudentGroups;
    @FXML
    private TableView<StudentGroupTableEntry> tableStudentGroups;
    private ObservableList<StudentGroupTableEntry> validStudentGroups = FXCollections.observableArrayList();

    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final SubjectDao subjectDao;
    private final TeacherSubjectLinkDao teacherSubjectLinkDao;
    private final StudentGroupDao studentGroupDao;
    private final StudentGroupSubjectLinkDao studentGroupSubjectLinkDao;
    private final StudentGroupStudentLinkDao studentGroupStudentLinkDao;
    private final GradeDao gradeDao;
    private final AssignedGradeDao assignedGradeDao;

    private Admin admin;

    public AdminController() {
        ServiceManager manager = ServiceManager.getInstance();
        studentDao = manager.getDAO(StudentDao.class);
        teacherDao = manager.getDAO(TeacherDao.class);
        subjectDao = manager.getDAO(SubjectDao.class);
        teacherSubjectLinkDao = manager.getDAO(TeacherSubjectLinkDao.class);
        studentGroupDao = manager.getDAO(StudentGroupDao.class);
        studentGroupSubjectLinkDao = manager.getDAO(StudentGroupSubjectLinkDao.class);
        studentGroupStudentLinkDao = manager.getDAO(StudentGroupStudentLinkDao.class);
        gradeDao = manager.getDAO(GradeDao.class);
        assignedGradeDao = manager.getDAO(AssignedGradeDao.class);
    }

//    @FXML
//    public void onDebug(ActionEvent event) {
//        MemoryDataPool pool = MemoryDataPool.instance;
//
//        System.out.println("getLecturerStatuses() // " + pool.getLecturerStatuses().size() + " :: " + pool.getLecturerStatuses());
//        System.out.println("getAdmins() // " + pool.getAdmins().size() + " :: " + pool.getAdmins());
//        System.out.println("getStudents() // " + pool.getStudents().size() + " :: " + pool.getStudents());
//        System.out.println("getStudentGroups() // " + pool.getStudentGroups().size() + " :: " + pool.getStudentGroups());
//        System.out.println("getStudentToStudentGroupMapping() // " + pool.getStudentToStudentGroupMapping().size() + " :: " + pool.getStudentToStudentGroupMapping());
//        System.out.println("getSubjects() // " + pool.getSubjects().size() + " :: " + pool.getSubjects());
//        System.out.println("getTeacherToSubjectMapping() // " + pool.getTeacherToSubjectMapping().size() + " :: " + pool.getTeacherToSubjectMapping());
//        System.out.println("getTeachers() // " + pool.getTeachers().size() + " :: " + pool.getTeachers());
//        System.out.println("getStudentGroupToSubjectMapping() // " + pool.getStudentGroupToSubjectMapping().size() + " :: " + pool.getStudentGroupToSubjectMapping());
//        System.out.println("getGrades() // " + pool.getGrades().size() + " :: " + pool.getGrades());
//        System.out.println("getAssignedGrades() // " + pool.getAssignedGrades().size() + " :: " + pool.getAssignedGrades());
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeStudentTable();
        initializeStudentGroupTable();
        initializeSubjectTable();
        initializeTeacherTable();

        initialized = true;
        switch (LAST_ACTIVE_TAB_VIEW) {
            case 0 -> table.getSelectionModel().select(tabStudents);
            case 1 -> table.getSelectionModel().select(tabStudentGroups);
            case 2 -> table.getSelectionModel().select(tabSubjects);
            case 3 -> table.getSelectionModel().select(tabTeachers);
        }
    }

    private void initializeStudentTable() {
        mapTable(tableStudents, 0, "name");
        mapTable(tableStudents, 1, "lastName");
        mapTable(tableStudents, 2, "studentGroup");

        addColumnWithButtons(tableStudents,
                TableActionButtonFactory.createModifyButton(
                        StudentTableEntry.class, Student.class, StudentModifyController.class,
                        (data) -> studentDao.getById(data.getId()), validStudentsList,
                        "admin/student-modify.fxml", (controller, student) -> {
                            controller.setAdmin(admin);
                            controller.setStudent(student);
                        }),
                TableActionButtonFactory.createRemoveButton(
                        StudentTableEntry.class, Student.class,
                        (data) -> {
                            studentGroupStudentLinkDao.detachStudentFromStudentGroup(data.getId());
                            assignedGradeDao.unassignAllGradesByStudentId(data.getId());
                            studentDao.removeStudent(data.getId());
                            return true;
                        }, validStudentsList,
                        "Ar tikrai norite šalinti šį studentą ir jiem priskirtus pažymius?"
                )
        );

        tableStudents.setItems(validStudentsList);
    }

    private void loadStudentData() {
        AsyncTask.async(() -> {
            return studentGroupStudentLinkDao.getAllStudentsWithOptStudentGroups();
        }).then((entries) -> {
            validStudentsList.setAll(entries.stream().map(o -> {
                return new StudentTableEntry(
                        o.getStudent().getId(),
                        capitalize(o.getStudent().getName()),
                        capitalize(o.getStudent().getLastName()),
                        o.getStudentGroup() == null ? "-" : o.getStudentGroup().getTitle().toUpperCase(Locale.ROOT)
                );
            }).collect(Collectors.toList()));
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.WARNING, "Nepavyko užkrauti studentų sąrašo.");
        }).execute();
    }

    private void initializeStudentGroupTable() {
        mapTable(tableStudentGroups, 0, "title");

        addColumnWithButtons(tableStudentGroups,
                TableActionButtonFactory.createModifyButton(
                        StudentGroupTableEntry.class, StudentGroup.class, StudentGroupModifyController.class,
                        (data) -> studentGroupDao.getById(data.getId()), validStudentGroups,
                        "admin/student-group-modify.fxml", (controller, studentGroup) -> {
                            controller.setAdmin(admin);
                            controller.setStudentGroup(studentGroup);
                        }),
                TableActionButtonFactory.createRemoveButton(
                        StudentGroupTableEntry.class, StudentGroup.class,
                        (data) -> {
                            studentGroupSubjectLinkDao.detachAllSubjectsFromStudentGroup(data.getId());
                            studentGroupStudentLinkDao.detachAllStudentsFromStudentGroup(data.getId());
                            studentGroupDao.removeStudentGroup(data.getId());
                            return true;
                        }, validStudentGroups,
                        "Ar tikrai norite šalinti šią studentų grupę?",
                        "Pastaba - studentų grupei priskirti studentai nebus pašalinti."
                )
        );

        tableStudentGroups.setItems(validStudentGroups);
    }

    private void loadStudentGroupData() {
        AsyncTask.async(() -> {
            return studentGroupDao.getAllStudentGroups();
        }).then((subjects) -> {
            validStudentGroups.setAll(subjects.stream().map(o -> {
                return new StudentGroupTableEntry(
                        o.getId(),
                        o.getTitle()
                );
            }).collect(Collectors.toList()));
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.WARNING, "Nepavyko užkrauti studentų grupių sąrašo.");
        }).execute();
    }

    private void initializeSubjectTable() {
        mapTable(tableSubjects, 0, "title");

        addColumnWithButtons(tableSubjects,
                TableActionButtonFactory.createModifyButton(
                        SubjectTableEntry.class, Subject.class, SubjectModifyController.class,
                        (data) -> subjectDao.getById(data.getId()), validSubjectList,
                        "admin/subject-modify.fxml", (controller, subject) -> {
                            controller.setAdmin(admin);
                            controller.setSubject(subject);
                        }),
                TableActionButtonFactory.createRemoveButton(
                        SubjectTableEntry.class, Subject.class,
                        (data) -> {
                            studentGroupSubjectLinkDao.detachAllStudentGroupsFromSubject(data.getId());
                            teacherSubjectLinkDao.detachTeachersFromSubject(data.getId());
                            assignedGradeDao.unassignAllGradesBySubjectId(data.getId());
                            gradeDao.removeGradesBySubjectId(data.getId());
                            subjectDao.removeSubject(data.getId());
                            return true;
                        }, validSubjectList,
                        "Ar tikrai norite šalinti šį dalyką ir visus su juo susijusius bendrus ir priskirtus pažymius?"
                )
        );

        tableSubjects.setItems(validSubjectList);
    }

    private void loadSubjectData() {
        AsyncTask.async(() -> {
            return subjectDao.getAllSubjects();
        }).then((subjects) -> {
            validSubjectList.setAll(subjects.stream().map(o -> {
                return new SubjectTableEntry(
                        o.getId(),
                        o.getTitle()
                );
            }).collect(Collectors.toList()));
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.WARNING, "Nepavyko užkrauti dalykų sąrašo.");
        }).execute();
    }

    private void initializeTeacherTable() {
        mapTable(tableTeachers, 0, "status");
        mapTable(tableTeachers, 1, "name");
        mapTable(tableTeachers, 2, "lastName");
        mapTable(tableTeachers, 3, "subject");

        addColumnWithButtons(tableTeachers,
                TableActionButtonFactory.createModifyButton(
                        TeacherTableEntry.class, Teacher.class, TeacherModifyController.class,
                        (data) -> teacherDao.getById(data.getId()), validTeacherList,
                        "admin/teacher-modify.fxml", (controller, teacher) -> {
                            controller.setAdmin(admin);
                            controller.setTeacher(teacher);
                        }),
                TableActionButtonFactory.createRemoveButton(
                        TeacherTableEntry.class, Teacher.class,
                        (data) -> {
                            teacherSubjectLinkDao.detachSubjectFromTeacher(data.getId());
                            teacherDao.removeTeacher(data.getId());
                            return true;
                        }, validTeacherList,
                        "Ar tikrai norite šalinti šį dėstytoją?"
                )
        );

        tableTeachers.setItems(validTeacherList);
    }

    private void loadTeacherData() {
        AsyncTask.async(() -> {
            return teacherSubjectLinkDao.getAllTeachersWithOptSubjects();
        }).then((teachers) -> {
            validTeacherList.setAll(teachers.stream().map(o -> {
                return new TeacherTableEntry(
                        o.getTeacher().getId(),
                        o.getTeacher().getLecturerStatus().getAbbreviation(),
                        capitalize(o.getTeacher().getName()),
                        capitalize(o.getTeacher().getLastName()),
                        o.getSubject() == null ? "-" : o.getSubject().getTitle()
                );
            }).collect(Collectors.toList()));
        }).exceptionally((ex) -> {
            ex.printStackTrace();
            AlertUtil.alert(AlertType.WARNING, "Nepavyko užkrauti dėstytotojų sąrašo.");
        }).execute();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        fieldUserName.setText(capitalize(admin.getName()) + " " + capitalize(admin.getLastName()));
    }

    public void onLogOut(ActionEvent event) throws Exception {
        SceneUtil.load(event, "login.fxml");
    }

    public void onCreateStudent(ActionEvent event) throws Exception {
        SceneUtil.load(event, "admin/student-create.fxml", StudentCreateController.class, (controller) -> {
            controller.setAdmin(admin);
        });
    }

    public void onCreateStudentGroup(ActionEvent event) throws Exception {
        SceneUtil.load(event, "admin/student-group-create.fxml", StudentGroupCreateController.class, (controller) -> {
            controller.setAdmin(admin);
        });
    }

    public void onCreateSubject(ActionEvent event) throws Exception {
        SceneUtil.load(event, "admin/subject-create.fxml", SubjectCreateController.class, (controller) -> {
            controller.setAdmin(admin);
        });
    }

    public void onCreateTeacher(ActionEvent event) throws Exception {
        SceneUtil.load(event, "admin/teacher-create.fxml", TeacherCreateController.class, (controller) -> {
            controller.setAdmin(admin);
        });
    }

    public void onFocusStudents() {
        if (initialized) LAST_ACTIVE_TAB_VIEW = 0;
        loadStudentData();
    }

    public void onFocusStudentGroups() {
        if (initialized) LAST_ACTIVE_TAB_VIEW = 1;
        loadStudentGroupData();
    }

    public void onFocusSubjects() {
        if (initialized) LAST_ACTIVE_TAB_VIEW = 2;
        loadSubjectData();
    }

    public void onFocusTeachers() {
        if (initialized) LAST_ACTIVE_TAB_VIEW = 3;
        loadTeacherData();
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

    public static class TeacherTableEntry {

        private final long id;
        private String status;
        private String name;
        private String lastName;
        private String subject;

        public TeacherTableEntry(long id, String status, String name, String lastName, String subject) {
            this.id = id;
            this.status = status;
            this.name = name;
            this.lastName = lastName;
            this.subject = subject;
        }

        public long getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

    }

    public static class SubjectTableEntry {

        private final int id;
        private String title;

        public SubjectTableEntry(int id, String title) {
            this.id = id;
            this.title = title;
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

    }

    public static class StudentTableEntry {

        private final long id;
        private String name;
        private String lastName;
        private String studentGroup;

        public StudentTableEntry(long id, String name, String lastName, String studentGroup) {
            this.id = id;
            this.name = name;
            this.lastName = lastName;
            this.studentGroup = studentGroup;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getStudentGroup() {
            return studentGroup;
        }

        public void setStudentGroup(String studentGroup) {
            this.studentGroup = studentGroup;
        }

    }

    public static class StudentGroupTableEntry {

        private final int id;
        private String title;

        public StudentGroupTableEntry(int id, String title) {
            this.id = id;
            this.title = title;
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

    }

}
