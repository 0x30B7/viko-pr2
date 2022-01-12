package dev.mantas.vikop2app.data.dao.impl.sql;

import dev.mantas.vikop2app.data.dao.type.LecturerStatusDao;
import dev.mantas.vikop2app.data.source.impl.SQLDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.List;

public class SQLLecturerStatusDao implements LecturerStatusDao {

    private final SQLDataSource dataSource;
    private DataSource source;

    public SQLLecturerStatusDao(@NotNull SQLDataSource dataSource) {
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
    public List<LecturerStatus> getStatues() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

}
