package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class MongoStudentDao implements StudentDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> studentCol;

    public MongoStudentDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        studentCol = database.getCollection(MongoConstants.COLLECTION_STUDENT);
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return (int) studentCol.countDocuments();
    }

    @Override
    public Student getById(long id) throws DatabaseException {
        Document result = studentCol.find(eq("id", id)).first();

        if (result == null) {
            return null;
        }

        return resolveStudent(result);
    }

    @Override
    public Student getByNameAndLastName(String name, String lastName) throws DatabaseException {
        Document result = studentCol.find(new Document()
                .append("name", name)
                .append("lastName", lastName)
        ).first();

        if (result == null) {
            return null;
        }

        return resolveStudent(result);
    }

    @Override
    public void createStudent(long id, Date createdAt, String name, String lastName) throws DatabaseException {
        studentCol.insertOne(new Document()
                .append("id", id)
                .append("createdAt", createdAt)
                .append("name", name)
                .append("lastName", lastName)
        );
    }

    @Override
    public void updateStudent(long id, @Nullable String name, @Nullable String lastName) throws DatabaseException {
        studentCol.updateOne(eq("id", id), new Document("$set", new Document()
                .append("name", name)
                .append("lastName", lastName)
        ));
    }

    @Override
    public void removeStudent(long id) throws DatabaseException {
        studentCol.deleteOne(eq("id", id));
    }

    // ======================================================================================================== //

    public static Student resolveStudent(Document doc) {
        return new Student(
                doc.getLong("id"),
                doc.getDate("createdAt"),
                doc.getString("name"),
                doc.getString("lastName")
        );
    }

}
