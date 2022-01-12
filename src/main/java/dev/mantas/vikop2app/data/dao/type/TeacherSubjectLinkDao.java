package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.TeacherWithSubject;

import java.util.List;

public interface TeacherSubjectLinkDao extends DataAccessObject {

    List<TeacherWithSubject> getAllTeachersWithOptSubjects() throws DatabaseException;

    TeacherWithSubject getByName(String name, String lastName) throws DatabaseException;

    Subject getSubjectByTeacherId(long teacherId) throws DatabaseException;

    Teacher getTeacherBySubjectId(int subjectId) throws DatabaseException;

    void attachSubjectToTeacher(long teacherId, int subjectId) throws DatabaseException;

    void detachSubjectFromTeacher(long teacherId) throws DatabaseException;

    void detachTeachersFromSubject(int subjectId) throws DatabaseException;

}
