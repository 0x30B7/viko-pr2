package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

public class SQLGradeDao implements GradeDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLGradeDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public List<Grade> getGradesBySubject(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GradeWithSubject getGradeWithSubject(int gradeId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Grade getById(int id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Grade getGradeByTitleAndSubjectId(String title, int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createGrade(Date createdAt, String title, int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGrade(int id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGradesBySubjectId(int subjectId) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
