package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLSubjectDao implements SubjectDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLSubjectDao(@NotNull SQLDataSource dataSource) {
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
    public List<Subject> getAllSubjects() throws DatabaseException {
        try (Connection conn = source.getConnection()) {
            PreparedStatement subjectsWithTeacher = conn.prepareStatement(
                    """
                            SELECT
                            	subject.id AS s_id,
                                subject.title AS s_title,
                                CASE
                            		WHEN subject.id IN (SELECT subject_id FROM teacher_subject_link) THEN teacher_subject_link.user_id
                            		ELSE NULL END
                                as teacher_id
                            FROM
                                subject, teacher_subject_link
                            """
            );
            ResultSet subjectQuery = subjectsWithTeacher.executeQuery();

            List<Subject> result = new ArrayList<>();
            while (subjectQuery.next()) {
                int subjectId = subjectQuery.getInt("s_id");
                String subjectTitle = subjectQuery.getString("s_title");
                Object optSubjectTeacherId = subjectQuery.getObject("teacher_id");

                result.add(new Subject(
                        subjectId,
                        subjectTitle
                ));
            }

            return result;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public Subject getById(int id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Subject getSubjectByTitle(String title) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createSubject(String title) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSubject(int id, String title) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSubject(int id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
