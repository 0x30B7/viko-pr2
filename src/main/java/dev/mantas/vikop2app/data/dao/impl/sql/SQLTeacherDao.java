package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.TeacherDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Teacher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.Date;

public class SQLTeacherDao implements TeacherDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLTeacherDao(@NotNull SQLDataSource dataSource) {
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
    public Teacher getById(long id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Teacher getByNameAndLastName(String name, String lastName) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createTeacher(long id, Date createdAt, String name, String lastName, LecturerStatus lecturerStatus,
                              @Nullable String email, @Nullable Integer roomNo) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTeacher(long id, @Nullable String name, @Nullable String lastName,
                              @Nullable LecturerStatus lecturerStatus, @Nullable String email,
                              @Nullable Integer roomNo) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeTeacher(long id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
