package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.helper.GradeWithAssignedGrade;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AssignedGradeDao extends DataAccessObject {

    Map<Integer, Integer> getAssignedGradeCountInStudentGroupsById(int gradeId, List<Integer> studentGroupIds) throws DatabaseException;

    List<AssignedGrade> getAssignedGrades(int gradeId, int studentGroupId) throws DatabaseException;

    List<GradeWithAssignedGrade> getAssignedGradesForStudentAndSubject(long studentId, int subjectId) throws DatabaseException;

    void assignGrade(int gradeId, Date createdAt, long studentId, int value) throws DatabaseException;

    void unassignGrade(int gradeId, long studentId) throws DatabaseException;

    void unassignAllAssignedGrades(int gradeId) throws DatabaseException;

    void unassignAllGradesByStudentId(long studentId) throws DatabaseException;

    void unassignAllGradesBySubjectId(int subjectId) throws DatabaseException;

}
