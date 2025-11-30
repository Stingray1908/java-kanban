package httptaskserver.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTaskHttpHandler<T> extends BaseHttpHandler<T> {

    protected AbstractTaskHttpHandler(InMemoryTaskManager manager, Gson gson, Class<T> typeClass, int maxPathLength) {
        super(manager, gson, typeClass, maxPathLength);
    }

    protected int id;
    protected T task = null;

    public void handle(HttpExchange exchange) {
        super.handle(exchange);
    }

    protected void executeGET() {
        if (splitPath.length <= 3) {
            if (id == -1) {
                getListTasks();
                toStringJson(list);
            } else {
                getTaskById();
                toStringJson(task);
            }
            rCode = 200;
        }
    }

    protected void executePOST(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement element = JsonParser.parseString(json);

            if (!element.isJsonObject()) throw new NotFoundException("The element is not Json object");
            fromStringJson(element);
        } catch (NumberFormatException e) {
            throw new IOException("Parsing from Json err");
        }
        if (id == -1) createNewTask();
        if (id > -1) upDateTask();
        rCode = 201;
    }

    protected void executeDELETE() {
        removeTaskById();
        rCode = 201;
    }

    @Override
    protected void isPathValid() {
        super.isPathValid();
        try {
            if (splitPath.length >= 3) id = Integer.parseInt(splitPath[2]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Id indicated in Path isn't digit");
        }
    }

    protected void process(HttpExchange exchange) throws IOException {
        switch (method) {
            case "GET":
                executeGET();
                break;
            case "POST":
                executePOST(exchange);
                break;
            case "DELETE":
                executeDELETE();
                break;
            default:
                throw new NotFoundException("Wrong method");
        }
    }

    protected void fromStringJson(JsonElement element) {
        task = gson.fromJson(element.getAsJsonObject(), typeClass);
    }

    protected void init(HttpExchange exchange) {
        super.init(exchange);
        id = -1;
        task = null;
    }

    protected abstract void getListTasks();

    protected abstract void getTaskById();

    protected abstract void createNewTask();

    protected abstract void upDateTask();

    protected abstract void removeTaskById();
}