package dev.mantas.vikop2app.data.source;

import dev.mantas.vikop2app.data.source.provider.DataSourceProvider;

public abstract class DataSourceContainer<D> implements AutoCloseable {

    private final DataSourceProvider<D> dataSourceProvider;
    protected D dataSource;

    public DataSourceContainer(DataSourceProvider<D> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public final void init() throws Exception {
        if (dataSource != null) {
            return;
        }

        dataSource = dataSourceProvider.provide();
    }

    public D provide() {
        return dataSource;
    }

    protected abstract void doClose() throws Exception;

    @Override
    public final void close() throws Exception {
        if (dataSource != null) {
            doClose();
        }
    }

}
