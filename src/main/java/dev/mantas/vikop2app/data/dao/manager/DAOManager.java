package dev.mantas.vikop2app.data.dao.manager;

import dev.mantas.vikop2app.data.dao.DAOFactory;
import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.data.source.IDataSource;

import java.util.HashMap;
import java.util.Map;

public abstract class DAOManager<D extends IDataSource> {

    private final Map<Class<? extends DataAccessObject>, DataAccessObject> dataAccessObjectMapping = new HashMap<>();
    private final D dataSource;

    public DAOManager(D dataSource) {
        this.dataSource = dataSource;
    }

    public void initDataSource() throws Exception {
        dataSource.init();
        dataAccessObjectMapping.values().forEach(DataAccessObject::onAvailable);
    }

    public void closeDataSource() throws Exception {
        dataSource.close();
    }

    public <T extends DataAccessObject> void registerDAO(Class<T> abstraction, DAOFactory<T, D> factory) {
        dataAccessObjectMapping.put(abstraction, factory.create(dataSource));
    }

    public <T extends DataAccessObject> T getDAO(Class<T> abstraction) {
        DataAccessObject obj = dataAccessObjectMapping.get(abstraction);

        if (obj == null) {
            throw new IllegalStateException("DAO not registered! (" + abstraction.getName() + ")");
        }

        return (T) obj;
    }

}
