package dev.mantas.vikop2app;

import com.mongodb.client.MongoClient;

import java.io.Closeable;
import java.io.IOException;

public class MongoConnection implements Closeable {

    private final MongoClient client;

    public MongoConnection(MongoClient client) {
        this.client = client;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

}
