package dev.mantas.vikop2app.data.source.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.mantas.vikop2app.data.source.DataSourceContainer;
import dev.mantas.vikop2app.data.source.IDataSource;
import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public class SQLDataSource extends DataSourceContainer<HikariDataSource> implements IDataSource {

    public SQLDataSource(DataSourceProvider<HikariDataSource> provider) {
        super(provider);
    }

    @Override
    protected void doClose() throws Exception {
        dataSource.close();
    }

}
