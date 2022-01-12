package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.helper.StudentWithStudentGroup;

import java.util.List;
import java.util.Map;

public interface StudentGroupStudentLinkDao extends DataAccessObject {

    Map<Integer, Integer> getStudentCountInStudentGroupsByIds(List<Integer> studentGroupIds) throws DatabaseException;

    List<StudentWithStudentGroup> getAllStudentsWithOptStudentGroups() throws DatabaseException;

    List<Student> getStudentsByStudentGroupId(int studentGroupId) throws DatabaseException;

    StudentGroup getStudentGroupByStudentId(long studentId) throws DatabaseException;

    void attachStudentToStudentGroup(long studentId, int studentGroupId) throws DatabaseException;

    void detachStudentFromStudentGroup(long studentId) throws DatabaseException;

    void detachAllStudentsFromStudentGroup(int studentGroupId) throws DatabaseException;

}
