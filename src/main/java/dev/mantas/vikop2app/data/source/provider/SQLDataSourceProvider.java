package dev.mantas.vikop2app.data.source.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLDataSourceProvider implements DataSourceProvider<HikariDataSource> {

    private final HikariConfig config;

    public SQLDataSourceProvider(HikariConfig config) {
        this.config = config;
    }

    @Override
    public HikariDataSource provide() throws Exception {
        return new HikariDataSource(config);
    }

}
