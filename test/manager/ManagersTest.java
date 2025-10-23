package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    public static TaskManager manager = Managers.getDefault();

    private static <T extends Task> ArrayList<T> getTestList(List<T> tasks) {
        return (ArrayList<T>) tasks;
    }

    @Test
    void ListTasksShouldBeNotNullAfterActivated() {
        ArrayList<Task> tasks = getTestList(manager.getListTasks());
        assertNotNull(tasks);
    }

    @Test
    void ListEpicsShouldBeNotNullAfterActivated() {
        ArrayList<Epic> epics = getTestList(manager.getListEpics());
        assertNotNull(epics);
    }

    @Test
    void ListSubtasksShouldBeNotNullAfterActivated() {
        ArrayList<Subtask> subtasks = getTestList(manager.getListSubtasks());
        assertNotNull(subtasks);
    }

    @Test
    void ListHistoryShouldBeNotNullAfterActivated() {
        ArrayList<Task> history = getTestList(manager.getHistory());
        assertNotNull(history);
    }

}