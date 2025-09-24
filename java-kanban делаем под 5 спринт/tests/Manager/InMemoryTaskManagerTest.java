package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public static TaskManager manager  = Managers.getDefault();
    static Task task;
    static Epic epic;
    static Subtask subtask;

    private <T extends Task> void upDataTask(T newTask) {
        if (newTask instanceof Epic) {
            manager.upDateEpic((Epic) newTask) ;
        } else if (newTask instanceof Subtask) {
            manager.upDateSubtask((Subtask) newTask);
        } else {
                manager.upDateTask(newTask);
        }
    }

    @BeforeAll
    static void beforeALL() {
         task = manager.createNewTasks(new Task("Task"));
         epic = manager.createNewEpic(new Epic("Epic"));
         subtask = manager.createNewSubtask(new Subtask("Subtask", epic.getId()));
    }

    @Test
    void shouldNotChangeTaskAfterHaveGottenCreated() {
        Task testTask = new Task("Task");
        assertEquals(testTask.getName(), task.getName());
        assertEquals(testTask.getStatus(), task.getStatus());
    }

    @Test
    void shouldNotChangeEpicAfterHaveGottenCreated() {
        Epic testEpic = new Epic("Epic");
        assertEquals(testEpic.getName(), epic.getName());
        assertEquals(testEpic.getStatus(), epic.getStatus());
    }

    @Test
    void shouldNotChangeSubtaskAfterHaveGottenCreated() {
        Subtask testSubtask = new Subtask("Subtask", epic.getId());
        assertEquals(testSubtask.getName(), subtask.getName());
        assertEquals(testSubtask.getStatus(), subtask.getStatus());
    }

    @Test
    void shouldHaveTaskInManagerList() {
        assertTrue(manager.getListTasks().contains(task), "Список задач все еще пуст");
    }

    @Test
    void shouldHaveEpicInManagerList() {
        assertTrue(manager.getListEpics().contains(epic), "Список задач все еще пуст");
    }

    @Test
    void shouldHaveSubtaskInManagerList() {
        assertTrue(manager.getListSubtasks().contains(subtask), "Список задач все еще пуст");
    }

    @Test
    void shouldReturnTaskByIdentifier() {
        assertEquals(task, manager.getTaskByIdentifier(task.getId()));
    }

    @Test
    void shouldReturnEpicByIdentifier() {
        assertEquals(epic, manager.getEpicByIdentifier(epic.getId()));
    }

    @Test
    void shouldReturnSubtaskByIdentifier() {
        assertEquals(subtask, manager.getSubtaskByIdentifier(subtask.getId()));
    }

    @Test
    void shouldUpDateSubtaskAndReturnRightName() {
        upDataTask(new Subtask("NEW Subtask", Status.NEW, epic.getId(),subtask.getId()));
        String name = manager.getSubtaskByIdentifier(subtask.getId()).getName();

        assertEquals(1, manager.getListSubtasks().size());
        assertEquals("NEW Subtask", name);
    }

    @Test
    void shouldUpDateEpicWithoutProblem() {
        upDataTask(new Epic("NEW Epic", epic.getId()));
        String name = manager.getEpicByIdentifier(epic.getId()).getName();

        assertEquals(1, manager.getListEpics().size());
        assertEquals("NEW Epic", name);
    }

    @Test
    void shouldUpDateTaskWithoutProblem() {
        upDataTask(new Task("NEW Task", Status.NEW, task.getId()));
        String name = manager.getTaskByIdentifier(task.getId()).getName();

        assertEquals(1, manager.getListTasks().size());
        assertEquals("NEW Task", name);
    }

    @Test
    void shouldSaveTheSameDateAfterUsingSearchByIdentifier() {
        Task taskString = manager.getTaskByIdentifier(task.getId());
        Epic epicString = manager.getEpicByIdentifier(epic.getId());
        Subtask subtaskString = manager.getSubtaskByIdentifier(subtask.getId());
        List<Task> tasks = manager.getHistory();

        assertEquals(3, tasks.size(), "после активации поиска по ID, список истории корректно растет");
        assertEquals(taskString.toString(), task.toString(), "данные изменились");
        assertEquals(epicString.toString(), epic.toString(), "данные изменились");
        assertEquals(subtaskString.toString(), subtask.toString(), "данные изменились");
    }


}