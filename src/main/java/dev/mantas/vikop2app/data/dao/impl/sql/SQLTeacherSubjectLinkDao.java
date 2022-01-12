package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.TeacherWithSubject;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.List;

public class SQLTeacherSubjectLinkDao implements TeacherSubjectLinkDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLTeacherSubjectLinkDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public List<TeacherWithSubject> getAllTeachersWithOptSubjects() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TeacherWithSubject getByName(String name, String lastName) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Subject getSubjectByTeacherId(long teacherId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Teacher getTeacherBySubjectId(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void attachSubjectToTeacher(long teacherId, int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachSubjectFromTeacher(long teacherId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachTeachersFromSubject(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
