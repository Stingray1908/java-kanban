package httptaskserver.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import exceptions.NotAcceptableException;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public abstract class BaseHttpHandler<T> implements HttpHandler {

    protected final InMemoryTaskManager manager;
    protected final Gson gson;
    public final int maxPathLength;
    protected Class<T> typeClass;

    protected String path;
    protected String[] splitPath;
    protected String method;
    protected String response;
    protected int rCode;
    protected ArrayList<T> list;


    protected BaseHttpHandler(InMemoryTaskManager manager, Gson gson, Class<T> typeClass, int maxPathLength) {
        this.manager = manager;
        this.gson = gson;
        this.typeClass = typeClass;
        this.maxPathLength = maxPathLength;
    }

    public void handle(HttpExchange exchange) {
        init(exchange);
        try {
            isPathValid();
            process(exchange);

        } catch (NotFoundException | NumberFormatException e) {
            sendNotFound(exchange);
            System.out.println(e.getMessage());

        } catch (ManagerSaveException e) {
            sendText(exchange, 500, "Internal Server Error");
            System.out.println(e.getMessage());

        } catch (NotAcceptableException e) {
            sendHasOverlaps(exchange);
            System.out.println(e.getMessage());

        } catch (IOException e) {
            sendText(exchange, 500, "Internal Server Error");
            System.out.println("Ошибка ввода-вывода");
        }

        sendText(exchange, rCode, response);
    }

    protected void sendText(HttpExchange exchange, int rCode, String response) {
        byte[] bytes = new byte[0];

        try {
            if (response == null) {
                exchange.sendResponseHeaders(rCode, -1);
                exchange.close();
                return;
            }

            bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(rCode, bytes.length);
        } catch (IOException e) {
            sendText(exchange, 500, "Internal Server Error");
            System.out.println("sending Headers error");
        }

        try (exchange; OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        } catch (IOException e) {
            sendText(exchange, 500, "Internal Server Error");
            System.out.println("write error");
        }
    }

    protected void sendNotFound(HttpExchange exchange) {
        sendText(exchange, 404, "Not Found");
    }

    protected void sendHasOverlaps(HttpExchange exchange) {
        sendText(exchange, 406, "Not Acceptable");
    }

    protected void init(HttpExchange exchange) {
        method = exchange.getRequestMethod();
        path = exchange.getRequestURI().getPath();
        splitPath = path.split("/");
        rCode = -1;
        response = null;
        list = null;
    }

    protected void isPathValid() {
        if (splitPath.length > maxPathLength) throw new NotFoundException("URL isn't correct");
    }

    protected <V> void toStringJson(V t) {
        response = gson.toJson(t);
    }

    protected abstract void process(HttpExchange exchange) throws IOException;

    protected abstract void executeGET();
}

