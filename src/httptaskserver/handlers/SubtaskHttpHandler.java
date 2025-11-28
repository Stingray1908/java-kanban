package httptaskserver.handlers;

import com.google.gson.Gson;
import exceptions.NotAcceptableException;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import tasks.Subtask;

public class SubtaskHttpHandler extends AbstractTaskHttpHandler<Subtask> {

    public SubtaskHttpHandler(InMemoryTaskManager manager, Gson gson) {
        super(manager, gson, Subtask.class, 3);
    }

    @Override
    protected void getListTasks() {
        list = manager.getListSubtasks();
    }

    @Override
    protected void getTaskById() {
        task = manager.getSubtaskByIdentifier(id);
    }

    @Override
    protected void createNewTask() {
        manager.createNewSubtask(task);
    }

    @Override
    protected void upDateTask() {
        if (id != task.getId())
            throw new NotAcceptableException("Task ID:" + id + "and Path ID:" + task.getId() + " don't match");
        manager.upDateSubtask(task);
    }

    @Override
    protected void removeTaskById() {
        if (!manager.removeSubtaskByIdentifier(id)) throw new NotFoundException("There is nothing to DELETE, id:" + id);
    }
}
