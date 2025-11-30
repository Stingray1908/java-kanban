package httptaskserver.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import tasks.Task;

public class HistoryHttpHandler extends BaseHttpHandler<Task> {

    public HistoryHttpHandler(InMemoryTaskManager manager, Gson gson) {
        super(manager, gson, Task.class, 2);
    }

    @Override
    protected void process(HttpExchange exchange) {
        if (!method.equals("GET")) throw new NotFoundException("Wrong method");
        executeGET();
    }

    @Override
    protected void executeGET() {
        toStringJson(manager.getHistory());
        rCode = 200;
    }
}

