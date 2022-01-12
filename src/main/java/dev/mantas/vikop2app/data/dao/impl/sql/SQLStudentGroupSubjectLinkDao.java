package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.List;

public class SQLStudentGroupSubjectLinkDao implements StudentGroupSubjectLinkDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLStudentGroupSubjectLinkDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public List<Subject> getSubjectsByStudentGroupId(int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StudentGroup> getStudentGroupsBySubjectId(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void attachSubjectsToStudentGroup(List<Integer> subjectIds, int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachAllSubjectsFromStudentGroup(int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachAllStudentGroupsFromSubject(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
