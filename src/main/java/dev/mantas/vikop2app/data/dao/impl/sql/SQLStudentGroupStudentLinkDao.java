package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.helper.StudentWithStudentGroup;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class SQLStudentGroupStudentLinkDao implements StudentGroupStudentLinkDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLStudentGroupStudentLinkDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public Map<Integer, Integer> getStudentCountInStudentGroupsByIds(List<Integer> studentGroupIds) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StudentWithStudentGroup> getAllStudentsWithOptStudentGroups() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Student> getStudentsByStudentGroupId(int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public StudentGroup getStudentGroupByStudentId(long studentId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void attachStudentToStudentGroup(long studentId, int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachStudentFromStudentGroup(long studentId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachAllStudentsFromStudentGroup(int studentGroupId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}

