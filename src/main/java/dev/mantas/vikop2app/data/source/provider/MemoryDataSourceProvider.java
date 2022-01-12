package dev.mantas.vikop2app.data.source.provider;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;

public class MemoryDataSourceProvider implements DataSourceProvider<MemoryDataPool> {

    private final boolean populate;

    public MemoryDataSourceProvider(boolean populate) {
        this.populate = populate;
    }

    @Override
    public MemoryDataPool provide() {
        MemoryDataPool instance = new MemoryDataPool();

        if (populate) {
            instance.populate();
        }

        return instance;
    }

}
