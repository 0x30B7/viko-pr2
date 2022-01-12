package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.helper.GradeWithAssignedGrade;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SQLAssignedGradeDao implements AssignedGradeDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLAssignedGradeDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public Map<Integer, Integer> getAssignedGradeCountInStudentGroupsById(int gradeId, List<Integer> studentGroupIds) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AssignedGrade> getAssignedGrades(int gradeId, int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<GradeWithAssignedGrade> getAssignedGradesForStudentAndSubject(long studentId, int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void assignGrade(int gradeId, Date createdAt, long studentId, int value) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unassignGrade(int gradeId, long studentId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unassignAllAssignedGrades(int gradeId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unassignAllGradesByStudentId(long studentId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unassignAllGradesBySubjectId(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
