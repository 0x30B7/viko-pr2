package dev.mantas.vikop2app.data.dao.manager.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.mantas.vikop2app.data.dao.manager.DAOManager;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public class DAOManagerSQL extends DAOManager<SQLDataSource> {

    public DAOManagerSQL(DataSourceProvider<HikariDataSource> provider) {
        super(new SQLDataSource(provider));
    }

}
