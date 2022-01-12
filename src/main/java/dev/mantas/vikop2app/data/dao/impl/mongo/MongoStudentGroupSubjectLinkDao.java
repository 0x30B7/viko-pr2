package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoStudentGroupSubjectLinkDao implements StudentGroupSubjectLinkDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> studentGroupSubjectLinkCol;

    public MongoStudentGroupSubjectLinkDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        studentGroupSubjectLinkCol = database.getCollection(MongoConstants.COLLECTION_STUDENT_GROUP_SUBJECT_LINK);
    }

    @Override
    public List<Subject> getSubjectsByStudentGroupId(int studentGroupId) throws DatabaseException {
        List<Subject> result = new ArrayList<>();

        studentGroupSubjectLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document("studentGroupId", studentGroupId)),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_SUBJECT)
                        .append("localField", "subjectId")
                        .append("foreignField", "id")
                        .append("as", "subject")
                ),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("studentGroupId", 0)
                        .append("subjectId", 0)
                ),
                new Document("$unwind", "$subject")
        )).map(doc -> {
            return MongoSubjectDao.resolveSubject(doc.get("subject", Document.class));
        }).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public List<StudentGroup> getStudentGroupsBySubjectId(int subjectId) throws DatabaseException {
        List<StudentGroup> result = new ArrayList<>();

        studentGroupSubjectLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document("subjectId", subjectId)),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_STUDENT_GROUP)
                        .append("localField", "studentGroupId")
                        .append("foreignField", "id")
                        .append("as", "studentGroup")
                ),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("studentGroupId", 0)
                        .append("subjectId", 0)),
                new Document("$unwind", "$studentGroup")
        )).map(doc -> {
            return MongoStudentGroupDao.resolveStudentGroup(doc.get("studentGroup", Document.class));
        }).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public void attachSubjectsToStudentGroup(List<Integer> subjectIds, int studentGroupId) throws DatabaseException {
        studentGroupSubjectLinkCol.insertMany(subjectIds.stream().map(id -> {
            return new Document()
                    .append("subjectId", id)
                    .append("studentGroupId", studentGroupId);
        }).collect(Collectors.toList()));
    }

    @Override
    public void detachAllSubjectsFromStudentGroup(int studentGroupId) throws DatabaseException {
        studentGroupSubjectLinkCol.deleteMany(new Document("studentGroupId", studentGroupId));
    }

    @Override
    public void detachAllStudentGroupsFromSubject(int subjectId) throws DatabaseException {
        studentGroupSubjectLinkCol.deleteMany(new Document("subjectId", subjectId));
    }

}
