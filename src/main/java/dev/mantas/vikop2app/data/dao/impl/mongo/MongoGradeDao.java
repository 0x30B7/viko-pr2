package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class MongoGradeDao implements GradeDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> sequenceIdCol;
    private MongoCollection<Document> gradeCol;
    private MongoCollection<Document> subjectCol;

    public MongoGradeDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase source = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        sequenceIdCol = source.getCollection(MongoConstants.COLLECTION_SEQUENCE_ID);
        gradeCol = source.getCollection(MongoConstants.COLLECTION_GRADE);
        subjectCol = source.getCollection(MongoConstants.COLLECTION_SUBJECT);
    }

    @Override
    public List<Grade> getGradesBySubject(int subjectId) throws DatabaseException {
        List<Grade> result = new ArrayList<>();

        gradeCol.find(eq("subjectId", subjectId)).map(doc -> new Grade(
                doc.getInteger("id"),
                doc.getDate("createdAt"),
                doc.getString("title"),
                doc.getInteger("subjectId")
        )).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public GradeWithSubject getGradeWithSubject(int gradeId) throws DatabaseException {
        Grade grade = getById(gradeId);
        Subject subject = null;

        if (grade != null) {
            Document subjectResult = subjectCol.find(eq("id", grade.getSubjectId())).first();

            if (subjectResult != null) {
                subject = MongoSubjectDao.resolveSubject(subjectResult);
            }
        }

        return new GradeWithSubject(grade, subject);
    }

    @Override
    public Grade getById(int id) throws DatabaseException {
        Document result = gradeCol.find(eq("id", id)).first();

        if (result == null) {
            return null;
        }

        return resolveGrade(result);
    }

    @Override
    public Grade getGradeByTitleAndSubjectId(String title, int subjectId) throws DatabaseException {
        Document result = gradeCol.find(new Document()
                .append("title", title)
                .append("subjectId", subjectId)
        ).first();

        if (result == null) {
            return null;
        }

        return resolveGrade(result);
    }

    @Override
    public void createGrade(Date createdAt, String title, int subjectId) throws DatabaseException {
        Document result = sequenceIdCol.findOneAndUpdate(
                eq("collection", MongoConstants.COLLECTION_GRADE),
                inc("nextId", 1),
                new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER));

        if (result == null) {
            throw new DatabaseException("Nepavyko sukurti pažymio ID įrašo");
        }

        gradeCol.insertOne(new Document()
                .append("id", result.getInteger("nextId"))
                .append("createdAt", createdAt)
                .append("title", title)
                .append("subjectId", subjectId)
        );
    }

    @Override
    public void removeGrade(int id) throws DatabaseException {
        gradeCol.deleteOne(eq("id", id));
    }

    @Override
    public void removeGradesBySubjectId(int subjectId) throws DatabaseException {
        gradeCol.deleteOne(eq("subjectId", subjectId));
    }

    // ======================================================================================================== //

    public static Grade resolveGrade(Document doc) {
        return new Grade(
                doc.getInteger("id"),
                doc.getDate("createdAt"),
                doc.getString("title"),
                doc.getInteger("subjectId")
        );
    }

}
