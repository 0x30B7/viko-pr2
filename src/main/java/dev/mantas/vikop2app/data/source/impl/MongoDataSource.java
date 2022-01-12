package dev.mantas.vikop2app.data.source.impl;

import com.mongodb.client.MongoClient;
import dev.mantas.vikop2app.data.source.DataSourceContainer;
import dev.mantas.vikop2app.data.source.IDataSource;
import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public class MongoDataSource extends DataSourceContainer<MongoClient> implements IDataSource {

    public MongoDataSource(DataSourceProvider<MongoClient> provider) {
        super(provider);
    }

    @Override
    protected void doClose() throws Exception {
        dataSource.close();
    }

}
