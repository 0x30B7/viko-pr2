package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;

import java.util.List;

public interface SubjectDao extends DataAccessObject {

    int getTotalCount() throws DatabaseException;

    List<Subject> getAllSubjects() throws DatabaseException;

    Subject getById(int id) throws DatabaseException;

    Subject getSubjectByTitle(String title) throws DatabaseException;

    void createSubject(String title) throws DatabaseException;

    void updateSubject(int id, String title) throws DatabaseException;

    void removeSubject(int id) throws DatabaseException;

}
