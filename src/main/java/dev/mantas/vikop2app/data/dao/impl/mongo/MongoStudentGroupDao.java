package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class MongoStudentGroupDao implements StudentGroupDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> sequenceIdCol;
    private MongoCollection<Document> studentGroupCol;

    public MongoStudentGroupDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        sequenceIdCol = database.getCollection(MongoConstants.COLLECTION_SEQUENCE_ID);
        studentGroupCol = database.getCollection(MongoConstants.COLLECTION_STUDENT_GROUP);
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return (int) studentGroupCol.countDocuments();
    }

    @Override
    public List<StudentGroup> getAllStudentGroups() throws DatabaseException {
        List<StudentGroup> result = new ArrayList<>();

        studentGroupCol.find().map(MongoStudentGroupDao::resolveStudentGroup)
                .cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public StudentGroup getById(int id) throws DatabaseException {
        Document result = studentGroupCol.find(eq("id", id)).first();

        if (result == null) {
            return null;
        }

        return resolveStudentGroup(result);
    }

    @Override
    public StudentGroup getByTitle(String title) throws DatabaseException {
        Document result = studentGroupCol.find(eq("title", title)).first();

        if (result == null) {
            return null;
        }

        return resolveStudentGroup(result);
    }

    @Override
    public void createStudentGroup(String title, Date createdAt) throws DatabaseException {
        Document result = sequenceIdCol.findOneAndUpdate(
                eq("collection", MongoConstants.COLLECTION_STUDENT_GROUP),
                inc("nextId", 1),
                new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER));

        if (result == null) {
            throw new DatabaseException("Nepavyko sukurti studentų grupės ID įrašo");
        }

        studentGroupCol.insertOne(new Document()
                .append("id", result.getInteger("nextId"))
                .append("createdAt", createdAt)
                .append("title", title)
        );
    }

    @Override
    public void updateStudentGroup(int id, String title) throws DatabaseException {
        studentGroupCol.updateOne(eq("id", id), new Document("$set", new Document()
                .append("title", title)
        ));
    }

    @Override
    public void removeStudentGroup(long id) throws DatabaseException {
        studentGroupCol.deleteOne(eq("id", id));
    }

    // ======================================================================================================== //

    public static StudentGroup resolveStudentGroup(Document doc) {
        return new StudentGroup(
                doc.getInteger("id"),
                doc.getDate("createdAt"),
                doc.getString("title")
        );
    }

}
