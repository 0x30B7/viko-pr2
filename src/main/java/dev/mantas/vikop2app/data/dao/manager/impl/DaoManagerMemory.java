package dev.mantas.vikop2app.data.dao.manager.impl;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.manager.DAOManager;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public class DaoManagerMemory extends DAOManager<MemoryDataSource> {

    public DaoManagerMemory(DataSourceProvider<MemoryDataPool> provider) {
        super(new MemoryDataSource(provider));
    }

}
