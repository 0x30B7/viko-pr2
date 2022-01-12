package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.LecturerStatusDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class MongoLecturerStatusDao implements LecturerStatusDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> lecturerStatusCol;

    public MongoLecturerStatusDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        lecturerStatusCol = database.getCollection(MongoConstants.COLLECTION_LECTURER_STATUS);
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return (int) lecturerStatusCol.countDocuments();
    }

    @Override
    public List<LecturerStatus> getStatues() throws DatabaseException {
        List<LecturerStatus> result = new ArrayList<>();

        lecturerStatusCol.find().map(MongoLecturerStatusDao::resolveLecturerStatus)
                .cursor().forEachRemaining(result::add);

        return result;
    }

    // ======================================================================================================== //

    public static LecturerStatus resolveLecturerStatus(Document doc) {
        return new LecturerStatus(
                doc.getInteger("id"),
                capitalize(doc.getString("name")),
                capitalize(doc.getString("abbreviation"))
        );
    }

}
