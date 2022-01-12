package dev.mantas.vikop2app.data.dao;

import dev.mantas.vikop2app.data.source.IDataSource;

public interface DAOFactory<T extends DataAccessObject, D extends IDataSource> {

    T create(D dataSource);

}
