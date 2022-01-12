package dev.mantas.vikop2app.util.async;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncHelper {

    private static final ExecutorService ASYNC_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread t = new Thread(r);
            t.setName("Async Executor Thread");
            t.setDaemon(true);
            return t;
        }
    });

    public static void runAsync(Runnable task) {
        ASYNC_EXECUTOR.submit(task);
    }

    public static void runAsync(AsyncTask<?> task) {
        ASYNC_EXECUTOR.submit(task::run);
    }

}
