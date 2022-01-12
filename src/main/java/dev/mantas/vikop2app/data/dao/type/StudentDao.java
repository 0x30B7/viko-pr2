package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public interface StudentDao extends DataAccessObject {

    int getTotalCount() throws DatabaseException;

    Student getById(long id) throws DatabaseException;

    Student getByNameAndLastName(String name, String lastName) throws DatabaseException;

    void createStudent(long id, Date createdAt, String name, String lastName) throws DatabaseException;

    void updateStudent(long id, @Nullable String name, @Nullable String lastName) throws DatabaseException;

    void removeStudent(long id) throws DatabaseException;

}
