package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Teacher;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public interface TeacherDao extends DataAccessObject {

    int getTotalCount() throws DatabaseException;

    Teacher getById(long id) throws DatabaseException;

    Teacher getByNameAndLastName(String name, String lastName) throws DatabaseException;

    void createTeacher(long id, Date createdAt, String name, String lastName, LecturerStatus lecturerStatus,
                       @Nullable String email, @Nullable Integer roomNo) throws DatabaseException;

    void updateTeacher(long id, String name, String lastName,
                       LecturerStatus lecturerStatus, @Nullable String email,
                       @Nullable Integer roomNo) throws DatabaseException;

    void removeTeacher(long id) throws DatabaseException;

}
