package dev.mantas.vikop2app.util.async;

public interface AsyncTaskConsumer<T> {

    void accept(T t) throws Exception;

}
