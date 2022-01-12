package dev.mantas.vikop2app.data.source.provider;

public interface DataSourceProvider<D> {

    D provide() throws Exception;

}
