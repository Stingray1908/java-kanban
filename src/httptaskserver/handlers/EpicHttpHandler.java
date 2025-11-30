package httptaskserver.handlers;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import tasks.Epic;

public class EpicHttpHandler extends AbstractTaskHttpHandler<Epic> {

    public EpicHttpHandler(InMemoryTaskManager manager, Gson gson) {
        super(manager, gson, Epic.class, 4);
    }

    private boolean isGetSubtasks = false;

    @Override
    protected void isPathValid() {
        super.isPathValid();
        if (splitPath.length == 4) {
            if (!splitPath[3].equals("subtasks"))
                throw new NotFoundException("request isn't correct: " + '"' + splitPath[3] + '"');
            isGetSubtasks = true;
        }
    }

    @Override
    protected void executeGET() {
        super.executeGET();
        if (splitPath.length == 4 && isGetSubtasks) {
            toStringJson(manager.getEpicSubtasks(id));
            rCode = 200;
        }
    }

    @Override
    protected void getListTasks() {
        list = manager.getListEpics();
    }

    @Override
    protected void getTaskById() {
        task = manager.getEpicByIdentifier(id);
    }

    @Override
    protected void createNewTask() {
        manager.createNewEpic(task);
    }

    @Override
    protected void upDateTask() {
        if (id != task.getId())
            throw new NotFoundException("Task ID:" + id + "and Path ID:" + task.getId() + " don't match");
        manager.upDateEpic(task);
    }

    @Override
    protected void removeTaskById() {
        if (!manager.removeEpicByIdentifier(id)) throw new NotFoundException("There is nothing to DELETE, id:" + id);
    }
}


