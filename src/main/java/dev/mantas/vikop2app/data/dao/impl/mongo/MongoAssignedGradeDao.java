package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.helper.GradeWithAssignedGrade;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class MongoAssignedGradeDao implements AssignedGradeDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> assignedGradeCol;
    private MongoCollection<Document> gradeCol;
    private MongoCollection<Document> studentGroupStudentCol;

    public MongoAssignedGradeDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        assignedGradeCol = database.getCollection(MongoConstants.COLLECTION_ASSIGNED_GRADE);
        gradeCol = database.getCollection(MongoConstants.COLLECTION_GRADE);
        studentGroupStudentCol = database.getCollection(MongoConstants.COLLECTION_STUDENT_GROUP_STUDENT_LINK);
    }

    @Override
    public Map<Integer, Integer> getAssignedGradeCountInStudentGroupsById(int gradeId, List<Integer> studentGroupIds) throws DatabaseException {
        Map<Integer, Integer> result = new HashMap<>();

        assignedGradeCol.aggregate(Arrays.asList(
                        new Document("$match", new Document("gradeId", gradeId)),
                        new Document("$lookup", new Document()
                                .append("from", MongoConstants.COLLECTION_GRADE)
                                .append("localField", "gradeId")
                                .append("foreignField", "id")
                                .append("as", "grade")),
                        new Document("$match", new Document("grade.0", new Document("$exists", true))),
                        new Document("$lookup", new Document()
                                .append("from", MongoConstants.COLLECTION_STUDENT_GROUP_STUDENT_LINK)
                                .append("localField", "studentId")
                                .append("foreignField", "studentId")
                                .append("as", "studentLink")),
                        new Document("$match", new Document("studentLink.studentGroupId", new Document("$in", studentGroupIds))),
                        new Document("$unwind", "$studentLink"),
                        new Document("$group", new Document()
                                .append("_id", "$studentLink.studentGroupId")
                                .append("count", new Document("$count", new Document())))
                ))
                .cursor().forEachRemaining(doc -> {
                    result.put(doc.getInteger("_id"), doc.getInteger("count"));
                });

        return result;
    }

    @Override
    public List<AssignedGrade> getAssignedGrades(int gradeId, int studentGroupId) throws DatabaseException {
        List<AssignedGrade> result = new ArrayList<>();

        studentGroupStudentCol.aggregate(Arrays.asList(
                        new Document("$match", new Document("studentGroupId", studentGroupId)),
                        new Document("$lookup", new Document()
                                .append("from", MongoConstants.COLLECTION_ASSIGNED_GRADE)
                                .append("as", "assignedGrades")
                                .append("let", new Document()
                                        .append("studentId", "$studentId"))
                                .append("pipeline", Arrays.asList(
                                        new Document("$match", new Document()
                                                .append("$expr", new Document("$eq", Arrays.asList(
                                                        "$$studentId", "$studentId"
                                                )))
                                                .append("gradeId", gradeId))
                                )))
                ))
                .cursor().forEachRemaining(doc -> {
                    List<Document> assignedGrades = doc.getList("assignedGrades", Document.class);

                    if (!assignedGrades.isEmpty()) {
                        result.addAll(assignedGrades.stream()
                                .map(MongoAssignedGradeDao::resolveAssignedGrade)
                                .collect(Collectors.toList()));
                    }
                });

        return result;
    }

    @Override
    public List<GradeWithAssignedGrade> getAssignedGradesForStudentAndSubject(long studentId, int subjectId) throws DatabaseException {
        List<GradeWithAssignedGrade> result = new ArrayList<>();

        assignedGradeCol.aggregate(Arrays.asList(
                new Document("$match", new Document("studentId", studentId)),
                new Document("$lookup", new Document()
                        .append("from", MongoConstants.COLLECTION_GRADE)
                        .append("localField", "gradeId")
                        .append("foreignField", "id")
                        .append("as", "grade")
                ),
                new Document("$match", new Document("grade.0.subjectId", subjectId))
        )).map(doc -> {
            AssignedGrade assignedGrade = resolveAssignedGrade(doc);
            Grade grade = null;

            List<Document> gradeField = doc.getList("grade", Document.class);
            if (!gradeField.isEmpty()) {
                Document gradeResult = gradeField.get(0);
                grade = MongoGradeDao.resolveGrade(gradeResult);
            }

            return new GradeWithAssignedGrade(grade, assignedGrade);
        }).cursor().forEachRemaining(result::add);

        return result;
    }

    @Override
    public void assignGrade(int gradeId, Date createdAt, long studentId, int value) throws DatabaseException {
        // TODO: check exists, then update

        assignedGradeCol.updateOne(new Document()
                        .append("gradeId", gradeId)
                        .append("studentId", studentId),
                Updates.combine(
                        new Document("$set", new Document("value", value)),
                        new Document("$setOnInsert", new Document("createdAt", createdAt))
                ),
                new UpdateOptions().upsert(true));
    }

    @Override
    public void unassignGrade(int gradeId, long studentId) throws DatabaseException {
        assignedGradeCol.deleteOne(new Document()
                .append("gradeId", gradeId)
                .append("studentId", studentId));
    }

    @Override
    public void unassignAllAssignedGrades(int gradeId) throws DatabaseException {
        assignedGradeCol.deleteMany(new Document("gradeId", gradeId));
    }

    @Override
    public void unassignAllGradesByStudentId(long studentId) throws DatabaseException {
        assignedGradeCol.deleteMany(new Document("studentId", studentId));
    }

    @Override
    public void unassignAllGradesBySubjectId(int subjectId) throws DatabaseException {
        List<Integer> gradeIds = new ArrayList<>();

        gradeCol.find(eq("subjectId", subjectId)).map(doc -> doc.getInteger("id"))
                .cursor().forEachRemaining(gradeIds::add);

        assignedGradeCol.deleteMany(in("gradeId", gradeIds));
    }

    // ======================================================================================================== //

    public static AssignedGrade resolveAssignedGrade(Document doc) {
        return new AssignedGrade(
                doc.getInteger("id", -1),
                doc.getDate("createdAt"),
                doc.getInteger("gradeId"),
                doc.getLong("studentId"),
                doc.getInteger("value")
        );
    }

}
