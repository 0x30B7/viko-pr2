package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.helper.StudentWithStudentGroup;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoStudentGroupStudentLinkDao implements StudentGroupStudentLinkDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> studentCol;
    private MongoCollection<Document> studentGroupStudentLinkCol;

    public MongoStudentGroupStudentLinkDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        studentCol = database.getCollection(MongoConstants.COLLECTION_STUDENT);
        studentGroupStudentLinkCol = database.getCollection(MongoConstants.COLLECTION_STUDENT_GROUP_STUDENT_LINK);
    }

    @Override
    public Map<Integer, Integer> getStudentCountInStudentGroupsByIds(List<Integer> studentGroupIds) throws DatabaseException {
        Map<Integer, Integer> result = new HashMap<>();

        studentGroupStudentLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document("studentGroupId", new Document("$in", studentGroupIds))),
                new Document("$group", new Document()
                        .append("_id", "$studentGroupId")
                        .append("count", new Document("$count", new Document())))
        )).cursor().forEachRemaining(doc -> {
            result.put(doc.getInteger("_id"), doc.getInteger("count"));
        });

        return result;
    }

    @Override
    public List<StudentWithStudentGroup> getAllStudentsWithOptStudentGroups() throws DatabaseException {
        Map<Integer, StudentGroup> studentGroupCache = new HashMap<>();
        List<StudentWithStudentGroup> result = new ArrayList<>();

        studentCol.aggregate(Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_STUDENT_GROUP_STUDENT_LINK)
                        .append("localField", "id")
                        .append("foreignField", "studentId")
                        .append("as", "groupLink")
                ),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_STUDENT_GROUP)
                        .append("localField", "groupLink.0.studentGroupId")
                        .append("foreignField", "id")
                        .append("as", "studentGroup")
                ),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("groupLink", 0)),
                new Document("$unwind", new Document()
                        .append("path", "$studentGroup")
                        .append("preserveNullAndEmptyArrays", true))
        )).map(doc -> {
            Student student = MongoStudentDao.resolveStudent(doc);
            StudentGroup group = null;

            Document groupField = doc.get("studentGroup", Document.class);
            if (groupField != null) {
                group = studentGroupCache.computeIfAbsent(groupField.getInteger("id"),
                        id -> MongoStudentGroupDao.resolveStudentGroup(groupField));
            }

            return new StudentWithStudentGroup(student, group);
        }).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public List<Student> getStudentsByStudentGroupId(int studentGroupId) throws DatabaseException {
        List<Student> result = new ArrayList<>();

        studentGroupStudentLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document("studentGroupId", studentGroupId)),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_STUDENT)
                        .append("localField", "studentId")
                        .append("foreignField", "id")
                        .append("as", "student")
                ),
                new Document("$unwind", "$student"),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("student", 1))
        )).map(doc -> {
            return MongoStudentDao.resolveStudent(doc.get("student", Document.class));
        }).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public StudentGroup getStudentGroupByStudentId(long studentId) throws DatabaseException {
        return studentGroupStudentLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document("studentId", studentId)),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_STUDENT_GROUP)
                        .append("localField", "studentGroupId")
                        .append("foreignField", "id")
                        .append("as", "studentGroup")
                ),
                new Document("$limit", 1),
                new Document("$unwind", "$studentGroup"),
                new Document("$project", new Document()
                        .append("_id", 0)
                        .append("studentGroup", 1))
        )).map(doc -> {
            return MongoStudentGroupDao.resolveStudentGroup(doc.get("studentGroup", Document.class));
        }).first();
    }

    @Override
    public void attachStudentToStudentGroup(long studentId, int studentGroupId) throws DatabaseException {
        Document doc = new Document()
                .append("studentId", studentId)
                .append("studentGroupId", studentGroupId);

        boolean exists = studentGroupStudentLinkCol.countDocuments(doc) > 0;
        if (!exists) {
            studentGroupStudentLinkCol.insertOne(doc);
        }
    }

    @Override
    public void detachStudentFromStudentGroup(long studentId) throws DatabaseException {
        studentGroupStudentLinkCol.deleteMany(new Document("studentId", studentId));
    }

    @Override
    public void detachAllStudentsFromStudentGroup(int studentGroupId) throws DatabaseException {
        studentGroupStudentLinkCol.deleteMany(new Document("studentGroupId", studentGroupId));
    }

}

