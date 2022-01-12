package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.TeacherDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Teacher;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.mongodb.client.model.Filters.eq;

public class MongoTeacherDao implements TeacherDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> teacherCol;
    private MongoCollection<Document> lecturerStatusCol;

    public MongoTeacherDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        teacherCol = database.getCollection(MongoConstants.COLLECTION_TEACHER);
        lecturerStatusCol = database.getCollection(MongoConstants.COLLECTION_LECTURER_STATUS);
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return (int) teacherCol.countDocuments();
    }

    @Override
    public Teacher getById(long id) throws DatabaseException {
        Document result = teacherCol.find(eq("id", id)).first();

        if (result == null) {
            return null;
        }

        return resolveTeacher(result, lecturerStatusCol, Collections.emptyMap());
    }

    @Override
    public Teacher getByNameAndLastName(String name, String lastName) throws DatabaseException {
        Document result = teacherCol.find(new Document()
                .append("name", name)
                .append("lastName", lastName)
        ).first();

        if (result == null) {
            return null;
        }

        return resolveTeacher(result, lecturerStatusCol, Collections.emptyMap());
    }

    @Override
    public void createTeacher(long id, Date createdAt, String name, String lastName, LecturerStatus lecturerStatus,
                              @Nullable String email, @Nullable Integer roomNo) throws DatabaseException {
        teacherCol.insertOne(new Document()
                .append("id", id)
                .append("createdAt", createdAt)
                .append("name", name)
                .append("lastName", lastName)
                .append("lecturerStatusId", lecturerStatus.getId())
                .append("email", email)
                .append("roomNumber", roomNo)
        );
    }

    @Override
    public void updateTeacher(long id, String name, String lastName,
                              LecturerStatus lecturerStatus, @Nullable String email,
                              @Nullable Integer roomNo) throws DatabaseException {
        teacherCol.updateOne(eq("id", id), new Document("$set", new Document()
                .append("name", name)
                .append("lastName", lastName)
                .append("lecturerStatusId", lecturerStatus.getId())
                .append("email", email)
                .append("roomNumber", roomNo)
        ));
    }

    @Override
    public void removeTeacher(long id) throws DatabaseException {
        teacherCol.deleteOne(eq("id", id));
    }

    // ======================================================================================================== //

    public static Teacher resolveTeacher(Document doc, MongoCollection<Document> lecturerStatusCol,
                                         Map<Integer, LecturerStatus> lecturerStatusCache) {
        Function<Integer, LecturerStatus> lecturerStatusQuery = id -> {
            Document lecturerStatusDoc = lecturerStatusCol.find(new Document("id", id)).first();
            return lecturerStatusDoc == null ? null : MongoLecturerStatusDao.resolveLecturerStatus(lecturerStatusDoc);
        };

        return new Teacher(
                doc.getLong("id"),
                doc.getDate("createdAt"),
                doc.getString("name"),
                doc.getString("lastName"),
                lecturerStatusCache == Collections.EMPTY_MAP ?
                        lecturerStatusQuery.apply(doc.getInteger("lecturerStatusId")) :
                        lecturerStatusCache.computeIfAbsent(doc.getInteger("lecturerStatusId"), lecturerStatusQuery),
                doc.getString("email"),
                doc.getInteger("roomNumber"));
    }

}
