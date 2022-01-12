package dev.mantas.vikop2app.data.source;

public interface IDataSource extends AutoCloseable {

    void init() throws Exception;

}
