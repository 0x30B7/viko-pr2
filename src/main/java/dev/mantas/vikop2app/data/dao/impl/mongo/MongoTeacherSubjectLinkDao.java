package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.TeacherWithSubject;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoTeacherSubjectLinkDao implements TeacherSubjectLinkDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> teacherCol;
    private MongoCollection<Document> lecturerStatusCol;
    private MongoCollection<Document> teacherSubjectLinkCol;

    public MongoTeacherSubjectLinkDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        teacherCol = database.getCollection(MongoConstants.COLLECTION_TEACHER);
        lecturerStatusCol = database.getCollection(MongoConstants.COLLECTION_LECTURER_STATUS);
        teacherSubjectLinkCol = database.getCollection(MongoConstants.COLLECTION_TEACHER_SUBJECT_LINK);
    }

    @Override
    public List<TeacherWithSubject> getAllTeachersWithOptSubjects() throws DatabaseException {
        Map<Integer, LecturerStatus> lecturerStatusCache = new HashMap<>();
        Map<Integer, Subject> subjectCache = new HashMap<>();
        List<TeacherWithSubject> result = new ArrayList<>();

        teacherCol.aggregate(Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_TEACHER_SUBJECT_LINK)
                        .append("localField", "id")
                        .append("foreignField", "teacherId")
                        .append("as", "subjectId")
                ),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_SUBJECT)
                        .append("localField", "subjectId.0.subjectId")
                        .append("foreignField", "id")
                        .append("as", "subject")
                ),
                new Document("$project", new Document("subjectId", 0))
        )).map(doc -> {
            Teacher teacher = MongoTeacherDao.resolveTeacher(doc, lecturerStatusCol, lecturerStatusCache);
            Subject subject = null;

            List<Document> subjectField = doc.getList("subject", Document.class);
            if (!subjectField.isEmpty()) {
                Document subjectResult = subjectField.get(0);
                subject = subjectCache.computeIfAbsent(subjectResult.getInteger("id"),
                        id -> MongoSubjectDao.resolveSubject(subjectResult));
            }

            return new TeacherWithSubject(teacher, subject);
        }).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public TeacherWithSubject getByName(String name, String lastName) throws DatabaseException {
        return teacherCol.aggregate(Arrays.asList(
                new Document("$match", new Document()
                        .append("name", name)
                        .append("lastName", lastName)),
                new Document("$limit", 1),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_TEACHER_SUBJECT_LINK)
                        .append("localField", "id")
                        .append("foreignField", "teacherId")
                        .append("as", "subjectId")
                ),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_SUBJECT)
                        .append("localField", "subjectId.0.subjectId")
                        .append("foreignField", "id")
                        .append("as", "subject")
                ),
                new Document("$project", new Document("subjectId", 0))
        )).map(doc -> {
            Teacher teacher = MongoTeacherDao.resolveTeacher(doc, lecturerStatusCol, Collections.emptyMap());
            Subject subject = null;

            List<Document> subjectField = doc.getList("subject", Document.class);
            if (!subjectField.isEmpty()) {
                Document subjectResult = subjectField.get(0);
                subject = MongoSubjectDao.resolveSubject(subjectResult);
            }

            return new TeacherWithSubject(teacher, subject);
        }).first();
    }

    @Override
    public Subject getSubjectByTeacherId(long teacherId) throws DatabaseException {
        return teacherSubjectLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document()
                        .append("teacherId", teacherId)),
                new Document("$limit", 1),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_SUBJECT)
                        .append("localField", "subjectId")
                        .append("foreignField", "id")
                        .append("as", "subject")
                ),
                new Document("$project", new Document()
                        .append("subject", 1)
                        .append("_id", 0)
                ),
                new Document("$unwind", "$subject")
        )).map(doc -> {
            return MongoSubjectDao.resolveSubject(doc.get("subject", Document.class));
        }).first();
    }

    @Override
    public Teacher getTeacherBySubjectId(int subjectId) throws DatabaseException {
        return teacherSubjectLinkCol.aggregate(Arrays.asList(
                new Document("$match", new Document()
                        .append("subjectId", subjectId)),
                new Document("$limit", 1),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_TEACHER)
                        .append("localField", "teacherId")
                        .append("foreignField", "id")
                        .append("as", "teacher")
                ),
                new Document("$project", new Document()
                        .append("teacher", 1)
                        .append("_id", 0)
                ),
                new Document("$unwind", "$teacher")
        )).map(doc -> {
            return MongoTeacherDao.resolveTeacher(doc.get("teacher", Document.class),
                    lecturerStatusCol, Collections.emptyMap());
        }).first();
    }

    @Override
    public void attachSubjectToTeacher(long teacherId, int subjectId) throws DatabaseException {
        Document doc = new Document()
                .append("teacherId", teacherId)
                .append("subjectId", subjectId);

        boolean exists = teacherSubjectLinkCol.countDocuments(doc) > 0;
        if (!exists) {
            teacherSubjectLinkCol.insertOne(doc);
        }
    }

    @Override
    public void detachSubjectFromTeacher(long teacherId) throws DatabaseException {
        teacherSubjectLinkCol.deleteMany(new Document("teacherId", teacherId));
    }

    @Override
    public void detachTeachersFromSubject(int subjectId) throws DatabaseException {
        teacherSubjectLinkCol.deleteMany(new Document("subjectId", subjectId));
    }

}
