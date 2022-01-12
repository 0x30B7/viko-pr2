package dev.mantas.vikop2app.data.source.provider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDataSourceProvider implements DataSourceProvider<MongoClient> {

    private final MongoClientSettings config;

    public MongoDataSourceProvider(MongoClientSettings config) {
        this.config = config;
    }

    @Override
    public MongoClient provide() {
        return MongoClients.create(config);
    }

}
