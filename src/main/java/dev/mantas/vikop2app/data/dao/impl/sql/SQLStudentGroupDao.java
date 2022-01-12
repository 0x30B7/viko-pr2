package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public class SQLStudentGroupDao implements StudentGroupDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLStudentGroupDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StudentGroup> getAllStudentGroups() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public StudentGroup getById(int id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public StudentGroup getByTitle(String title) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createStudentGroup(String title, Date createdAt) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateStudentGroup(int id, String title) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeStudentGroup(long id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
