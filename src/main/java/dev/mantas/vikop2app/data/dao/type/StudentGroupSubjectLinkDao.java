package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;

import java.util.List;

public interface StudentGroupSubjectLinkDao extends DataAccessObject {

    List<Subject> getSubjectsByStudentGroupId(int studentGroupId) throws DatabaseException;

    List<StudentGroup> getStudentGroupsBySubjectId(int subjectId) throws DatabaseException;

    void attachSubjectsToStudentGroup(List<Integer> subjectIds, int studentGroupId) throws DatabaseException;

    void detachAllSubjectsFromStudentGroup(int studentGroupId) throws DatabaseException;

    void detachAllStudentGroupsFromSubject(int subjectId) throws DatabaseException;

}
