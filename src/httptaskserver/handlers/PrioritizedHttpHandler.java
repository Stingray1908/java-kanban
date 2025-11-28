package httptaskserver.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;

import tasks.Task;

public class PrioritizedHttpHandler extends BaseHttpHandler<Task> {

    public PrioritizedHttpHandler(InMemoryTaskManager manager, Gson gson) {
        super(manager, gson, Task.class, 2);
    }

    protected void process(HttpExchange exchange) {
        if (!method.equals("GET")) throw new NotFoundException("Wrong method");
        executeGET();
    }

    @Override
    protected void executeGET() {
        toStringJson(manager.getPrioritizedTasks());
        rCode = 200;
    }
}