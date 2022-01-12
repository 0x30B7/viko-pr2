package dev.mantas.vikop2app.data.dao.manager.impl;

import com.mongodb.client.MongoClient;
import dev.mantas.vikop2app.data.dao.manager.DAOManager;
import dev.mantas.vikop2app.data.source.impl.MongoDataSource;
import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public class DAOManagerMongoDB extends DAOManager<MongoDataSource> {

    public DAOManagerMongoDB(DataSourceProvider<MongoClient> provider) {
        super(new MongoDataSource(provider));
    }

}
