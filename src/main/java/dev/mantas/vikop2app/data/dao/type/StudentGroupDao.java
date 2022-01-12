package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;

import java.util.Date;
import java.util.List;

public interface StudentGroupDao extends DataAccessObject {

    int getTotalCount() throws DatabaseException;

    List<StudentGroup> getAllStudentGroups() throws DatabaseException;

    StudentGroup getById(int id) throws DatabaseException;

    StudentGroup getByTitle(String title) throws DatabaseException;

    void createStudentGroup(String title, Date createdAt) throws DatabaseException;

    void updateStudentGroup(int id, String title) throws DatabaseException;

    void removeStudentGroup(long id) throws DatabaseException;

}
