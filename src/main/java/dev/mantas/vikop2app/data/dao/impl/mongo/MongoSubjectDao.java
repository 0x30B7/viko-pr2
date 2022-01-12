package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class MongoSubjectDao implements SubjectDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> sequenceIdCol;
    private MongoCollection<Document> subjectCol;

    public MongoSubjectDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        sequenceIdCol = database.getCollection(MongoConstants.COLLECTION_SEQUENCE_ID);
        subjectCol = database.getCollection(MongoConstants.COLLECTION_SUBJECT);
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return (int) subjectCol.countDocuments();
    }

    @Override
    public List<Subject> getAllSubjects() throws DatabaseException {
        List<Subject> result = new ArrayList<>();

        subjectCol.find().map(MongoSubjectDao::resolveSubject)
                .cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public Subject getById(int id) throws DatabaseException {
        Document result = subjectCol.find(eq("id", id)).first();

        if (result == null) {
            return null;
        }

        return resolveSubject(result);
    }

    @Override
    public Subject getSubjectByTitle(String title) throws DatabaseException {
        Document result = subjectCol.find(eq("title", title)).first();

        if (result == null) {
            return null;
        }

        return resolveSubject(result);
    }

    @Override
    public void createSubject(String title) throws DatabaseException {
        Document result = sequenceIdCol.findOneAndUpdate(
                eq("collection", MongoConstants.COLLECTION_SUBJECT),
                inc("nextId", 1),
                new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER));

        if (result == null) {
            throw new DatabaseException("Nepavyko sukurti dalyko ID įrašo");
        }

        subjectCol.insertOne(new Document()
                .append("id", result.getInteger("nextId"))
                .append("title", title)
        );
    }

    @Override
    public void updateSubject(int id, String title) throws DatabaseException {
        subjectCol.updateOne(eq("id", id), new Document("$set", new Document()
                .append("title", title))
        );
    }

    @Override
    public void removeSubject(int id) throws DatabaseException {
        subjectCol.deleteOne(eq("id", id));
    }

    // ======================================================================================================== //

    public static Subject resolveSubject(Document doc) {
        return new Subject(
                doc.getInteger("id"),
                doc.getString("title")
        );
    }

}
