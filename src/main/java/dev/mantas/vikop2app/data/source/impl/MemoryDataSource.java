package dev.mantas.vikop2app.data.source.impl;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.source.DataSourceContainer;
import dev.mantas.vikop2app.data.source.IDataSource;
import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public class MemoryDataSource extends DataSourceContainer<MemoryDataPool> implements IDataSource {

    public MemoryDataSource(DataSourceProvider<MemoryDataPool> provider) {
        super(provider);
    }

    @Override
    protected void doClose() throws Exception { }

}
