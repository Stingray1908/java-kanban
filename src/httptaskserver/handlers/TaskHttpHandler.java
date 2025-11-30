package httptaskserver.handlers;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import tasks.Task;

public class TaskHttpHandler extends AbstractTaskHttpHandler<Task> {

    public TaskHttpHandler(InMemoryTaskManager manager, Gson gson) {
        super(manager, gson, Task.class, 3);
    }

    @Override
    protected void getListTasks() {
        list = manager.getListTasks();
    }

    @Override
    protected void getTaskById() {
        task = manager.getTaskByIdentifier(id);
    }

    @Override
    protected void createNewTask() {
        manager.createNewTask(task);
    }

    @Override
    protected void upDateTask() {
        if (id != task.getId())
            throw new NotFoundException("Task ID:" + id + "and Path ID:" + task.getId() + " don't match");
        manager.upDateTask(task);
    }

    @Override
    protected void removeTaskById() {
        if (!manager.removeTaskByIdentifier(id)) throw new NotFoundException("There is nothing to DELETE, id:" + id);
    }

}

