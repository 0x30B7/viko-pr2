package dev.mantas.vikop2app.ui;

import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.type.AdminDao;
import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.ui.admin.AdminController;
import dev.mantas.vikop2app.ui.student.StudentController;
import dev.mantas.vikop2app.ui.teacher.TeacherController;
import dev.mantas.vikop2app.ui.util.AlertUtil;
import dev.mantas.vikop2app.ui.util.SceneUtil;
import dev.mantas.vikop2app.util.async.AsyncTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField fieldUsername;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Button btnLogin;

    private final StudentDao studentDao;
    private final StudentGroupStudentLinkDao studentGroupStudentLinkDao;
    private final TeacherSubjectLinkDao teacherSubjectLinkDao;
    private final AdminDao adminDao;

    public LoginController() {
        ServiceManager manager = ServiceManager.getInstance();
        studentDao = manager.getDAO(StudentDao.class);
        studentGroupStudentLinkDao = manager.getDAO(StudentGroupStudentLinkDao.class);
        teacherSubjectLinkDao = manager.getDAO(TeacherSubjectLinkDao.class);
        adminDao = manager.getDAO(AdminDao.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ServiceManager.getInstance().performInitialStateCheck();
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtil.alert(Alert.AlertType.ERROR, "Aplikacijos paleisti nepavyko:", ex.getMessage());
            System.exit(1);
        }
    }

    @FXML
    public void onLogin(ActionEvent event) throws IOException {
        String username = fieldUsername.getText();
        String password = fieldPassword.getText();

        if (username.isBlank() || password.isBlank())
            return;

        char userType = username.charAt(0);
        String realUsername = username.substring(1);

        fieldUsername.setDisable(true);
        fieldPassword.setDisable(true);
        btnLogin.setDisable(true);

        switch (userType) {
            case 's' -> {
                AsyncTask.async(() -> {
                    Student student = studentDao.getByNameAndLastName(
                            realUsername.toLowerCase(Locale.ROOT),
                            password.toLowerCase(Locale.ROOT));
                    if (student != null) {
                        return new Pair<>(student, studentGroupStudentLinkDao.getStudentGroupByStudentId(student.getId()));
                    }

                    return new Pair<>((Student) null, (StudentGroup) null);
                }).then((result) -> {
                    if (result.getKey() == null || result.getValue() == null) {
                        AlertUtil.alert(Alert.AlertType.ERROR,
                                "Prisijungimas nepavyko:",
                                "Neteisingi duomenys arba vartotojas nerastas arba studentas nepriskirtas studentų grupei.");
                        return;
                    }

                    SceneUtil.load(event, "student/home.fxml", StudentController.class, controller -> {
                        controller.setStudent(result.getKey());
                        controller.setStudentGroup(result.getValue());
                    });
                }).exceptionally((ex) -> {
                    ex.printStackTrace();
                    AlertUtil.alert(Alert.AlertType.ERROR,
                            "Prisijungimas nepavyko:",
                            "Įvyko vidinė klaida.");
                }).lastly(() -> {
                    fieldUsername.setDisable(false);
                    fieldPassword.setDisable(false);
                    btnLogin.setDisable(false);
                }).execute();
            }

            case 'd' -> {
                AsyncTask.async(() -> {
                    return teacherSubjectLinkDao.getByName(
                            realUsername.toLowerCase(Locale.ROOT),
                            password.toLowerCase(Locale.ROOT));
                }).then((teacherSubjectLink) -> {
                    if (teacherSubjectLink == null) {
                        AlertUtil.alert(Alert.AlertType.ERROR,
                                "Prisijungimas nepavyko:",
                                "Neteisingi duomenys arba vartotojas nerastas.");
                        return;
                    }

                    if (teacherSubjectLink.getSubject() == null) {
                        AlertUtil.alert(Alert.AlertType.ERROR,
                                "Prisijungimas nepavyko:",
                                "Jums nėra priskirtas dalykas.");
                        return;
                    }

                    SceneUtil.load(event, "teacher/home.fxml", TeacherController.class, controller -> {
                        controller.setTeacher(teacherSubjectLink.getTeacher());
                        controller.setSubject(teacherSubjectLink.getSubject());
                    });
                }).exceptionally((ex) -> {
                    ex.printStackTrace();
                    AlertUtil.alert(Alert.AlertType.ERROR,
                            "Prisijungimas nepavyko:",
                            "Įvyko vidinė klaida.");
                }).lastly(() -> {
                    fieldUsername.setDisable(false);
                    fieldPassword.setDisable(false);
                    btnLogin.setDisable(false);
                }).execute();
            }

            case 'a' -> {
                AsyncTask.async(() -> {
                    return adminDao.getByNameAndLastName(
                            realUsername.toLowerCase(Locale.ROOT),
                            password.toLowerCase(Locale.ROOT));
                }).then((admin) -> {
                    if (admin == null) {
                        AlertUtil.alert(Alert.AlertType.ERROR,
                                "Prisijungimas nepavyko:",
                                "Neteisingi duomenys arba vartotojas nerastas.");
                        return;
                    }

                    SceneUtil.load(event, "admin/home.fxml", AdminController.class, controller -> {
                        controller.setAdmin(admin);
                    });
                }).exceptionally((ex) -> {
                    ex.printStackTrace();
                    AlertUtil.alert(Alert.AlertType.ERROR,
                            "Prisijungimas nepavyko:",
                            "Įvyko vidinė klaida.");

                }).lastly(() -> {
                    fieldUsername.setDisable(false);
                    fieldPassword.setDisable(false);
                    btnLogin.setDisable(false);
                }).execute();
            }

            default -> {
                AlertUtil.alert(Alert.AlertType.ERROR,
                        "Prisijungimas nepavyko:",
                        "Neteisingi duomenys arba vartotojas nerastas.");
                fieldUsername.setDisable(false);
                fieldPassword.setDisable(false);
                btnLogin.setDisable(false);
            }
        }
    }

}