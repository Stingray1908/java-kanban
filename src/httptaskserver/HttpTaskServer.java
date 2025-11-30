package httptaskserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import httptaskserver.handlers.*;
import httptaskserver.adapters.DurationTypeAdapter;
import httptaskserver.adapters.LocalDateTimeTypeAdapter;
import manager.InMemoryTaskManager;
import manager.Managers;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;

public class HttpTaskServer {
    private HttpServer server;
    private final InMemoryTaskManager manager;
    private final int port = 8080;
    private final Gson gson;

    public HttpTaskServer(InMemoryTaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/tasks", new TaskHttpHandler(manager, gson));
            server.createContext("/epics", new EpicHttpHandler(manager, gson));
            server.createContext("/subtasks", new SubtaskHttpHandler(manager, gson));
            server.createContext("/history", new HistoryHttpHandler(manager, gson));
            server.createContext("/prioritized", new PrioritizedHttpHandler(manager, gson));
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");
            this.server = null;
        }
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен на порту " + port);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        InMemoryTaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager);

        server.start();
    }
}