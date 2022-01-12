package dev.mantas.vikop2app.data.dao.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.mantas.vikop2app.data.dao.impl.mongo.util.MongoConstants;
import dev.mantas.vikop2app.data.dao.type.AdminDao;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Admin;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class MongoAdminDao implements AdminDao {

    private final MongoDataSource dataSource;
    private MongoCollection<Document> adminCol;

    public MongoAdminDao(@NotNull MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MongoDatabase database = dataSource.provide().getDatabase(MongoConstants.DATABASE_NAME);
        adminCol = database.getCollection(MongoConstants.COLLECTION_ADMIN);
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return (int) adminCol.countDocuments();
    }

    @Override
    public Admin getByNameAndLastName(String name, String lastName) {
        Document first = adminCol.find(new Document().append("name", name).append("lastName", lastName)).limit(1).first();

        if (first == null) {
            return null;
        }

        return new Admin(
                first.getInteger("id"),
                first.getString("name"),
                first.getString("lastName"),
                first.getDate("createdAt")
        );
    }

}
