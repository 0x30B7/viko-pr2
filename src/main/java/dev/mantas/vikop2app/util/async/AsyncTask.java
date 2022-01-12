package dev.mantas.vikop2app.util.async;

import javafx.application.Platform;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class AsyncTask<T> {

    private final Callable<T> task;
    private AsyncTaskConsumer<T> onSuccess;
    private Consumer<Exception> onFailure = Exception::printStackTrace;
    private Runnable onFinally;

    public static <T> AsyncTask<T> async(Runnable task) {
        return new AsyncTask<>(() -> {
            task.run();
            return null;
        });
    }

    public static <T> AsyncTask<T> async(Callable<T> task) {
        return new AsyncTask<>(task);
    }

    private AsyncTask(Callable<T> task) {
        this.task = task;
    }

    public AsyncTask<T> then(Runnable onSuccess) {
        this.onSuccess = (nil) -> onSuccess.run();
        return this;
    }

    public AsyncTask<T> then(AsyncTaskConsumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public AsyncTask<T> exceptionally(Consumer<Exception> onFailure) {
        this.onFailure = onFailure;
        return this;
    }

    public AsyncTask<T> lastly(Runnable onFinally) {
        this.onFinally = onFinally;
        return this;
    }

    public void execute() {
        AsyncHelper.runAsync(this);
    }

    void run() {
        try {
            T result = task.call();
            if (onSuccess != null) {
                Platform.runLater(() -> {
                    try {
                        onSuccess.accept(result);
                    } catch (Exception ex) {
                        if (onFailure != null) {
                            Platform.runLater(() -> {
                                try {
                                    onFailure.accept(ex);
                                } catch (Exception fatalEx) {
                                    System.err.println("Exception occurred whilst handling error of async task during synchronization.");
                                    fatalEx.printStackTrace();
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                Platform.runLater(() -> {
                    try {
                        onFailure.accept(ex);
                    } catch (Exception fatalEx) {
                        System.err.println("Exception occurred whilst handling error of async task.");
                        fatalEx.printStackTrace();
                    }
                });
            }
        } finally {
            if (onFinally != null) {
                Platform.runLater(() -> {
                    try {
                        onFinally.run();
                    } catch (Exception ex) {
                        System.err.println("Exception occurred whilst handling error of async task onFinally clause.");
                        ex.printStackTrace();
                    }
                });
            }
        }
    }

}
