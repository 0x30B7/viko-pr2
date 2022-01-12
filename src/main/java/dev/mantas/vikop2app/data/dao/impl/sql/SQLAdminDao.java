package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.AdminDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Admin;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;

public class SQLAdminDao implements AdminDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLAdminDao(@NotNull SQLDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        source = dataSource.provide();
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Admin getByNameAndLastName(String name, String lastName) {
        throw new UnsupportedOperationException();
    }

}
