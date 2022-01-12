package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.UserType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class SQLStudentDao implements StudentDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLStudentDao(@NotNull SQLDataSource dataSource) {
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
    public Student getById(long id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Student getByNameAndLastName(String name, String lastName) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createStudent(long id, Date createdAt, String name, String lastName) throws DatabaseException {
        try (Connection conn = source.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(
                    """
                            INSERT INTO `user`
                                ('id', `type`, `name`, `last_name`, `created_at`) 
                            VALUES 
                                (?, ?, ?, ?, ?)
                            """
            );

            statement.setLong(1, id);
            statement.setInt(2, UserType.STUDENT.getId());
            statement.setString(3, name);
            statement.setString(4, lastName);
            statement.setTimestamp(5, Timestamp.from(createdAt.toInstant()));

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateStudent(long id, @Nullable String name, @Nullable String lastName) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeStudent(long id) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public Optional<Student> getStudentByNameSurname(String name, String lastName) {
//        try (Connection conn = dataSource.getConnection()) {
//            PreparedStatement subjectsWithTeacher = conn.prepareStatement(
//                    """
//                            SELECT
//                                id,
//                            	name,
//                            	last_name,
//                            	created_at,
//                            	CASE
//                            		WHEN subject.id IN (SELECT subject_id FROM teacher_subject_link) THEN teacher_subject_link.user_id
//                            		ELSE NULL END
//                                as teacher_id
//                            FROM
//                                student
//                            WHERE
//                                student.name = ? AND student.last_name = ?
//                            LIMIT 1
//                            """
//            );
//
//            subjectsWithTeacher.setString(1, name);
//            subjectsWithTeacher.setString(2, lastName);
//
//            ResultSet subjectQuery = subjectsWithTeacher.executeQuery();
//
//            if (!subjectQuery.next()) {
//                return Optional.empty();
//            }
//
//            int id = subjectQuery.getInt("id");
//            String studentName = subjectQuery.getString("name");
//            String studentLastName = subjectQuery.getString("last_name");
//            java.util.Date createdAt = subjectQuery.getTimestamp("created_at");
//
//            return Optional.of(new Student(id, studentName, studentLastName, createdAt));
//        } catch (SQLException ex) {
//            throw new DatabaseException(ex);
//        }
//    }

}
