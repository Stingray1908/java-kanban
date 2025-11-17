package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {

    final public static DateTimeFormatter END_TIME_FORMATTER = Task.START_TIME_FORMATTER;
    protected T manager;
    public static Task t_0;
    public static Epic e_1;
    public static Subtask s_2;
    public static Task task_0;
    public static Task task_4;
    public static Epic epic_1;
    public static Epic epic_5;
    public static Subtask subtask_2;
    public static Subtask subtask_3;
    public static Subtask subtask_6;
    public static LocalDateTime start_1;
    public static Duration duration_10m;
    public static Duration duration_30m;

    public static List<Task> task_List;
    public static List<Subtask> subtask_List;
    public static List<Integer> int_List;

    abstract T createManager();

    public void upDateTask_t_0() {
        t_0 = manager.upDateTask(new Task("Task", Status.DONE, task_0.getId(), start_1, Duration.ZERO));

    }

    public void upDateEpic_e_1() {
        e_1 = manager.upDateEpic(new Epic("Epic", epic_1.getId()));
    }

    public void upDateSubtask_s_2() {
        s_2 = manager.upDateSubtask(new Subtask(
                "Subtask", Status.DONE, subtask_2.getId(), start_1, Duration.ZERO, epic_1.getId()));
    }

    public boolean isTaskHaveAnyNullValues(Task task) {
        if (task instanceof Epic) {
            if (((Epic) task).getSubtasksList() == null) return true;
        }
        return (task.getName() == null &&
                task.getStatus() == null &&
                task.getEndTime() == null);
    }

    public boolean isTasksFullyEquals(Task t_1, Task t_2) {
        if (t_1 == null && t_2 == null) return true;
        if (!t_1.equals(t_2)) return false;

        if (t_1 instanceof Subtask) {
            if (((Subtask) t_1).getEpicKey() != ((Subtask) t_2).getEpicKey()) return false;
        }
        return (t_1.getName().equals(t_2.getName()) &&
                t_1.getStatus().equals(t_2.getStatus()));
    }

    @BeforeEach
    void beforeEach() {
        manager = createManager();
        start_1 = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        duration_10m = Duration.ofMinutes(10);
        duration_30m = Duration.ofMinutes(30);

        task_0 = manager.createNewTask(new Task("Task_0",
                LocalDateTime.of(2000, 2, 2, 2, 2, 2), duration_10m));
        epic_1 = manager.createNewEpic(new Epic("Epic_1"));
        subtask_2 = manager.createNewSubtask(new Subtask(
                "Subtask_2", LocalDateTime.of(2020, 3, 3, 3, 3, 3), duration_10m, 1));
        subtask_3 = manager.createNewSubtask(new Subtask(
                "Subtask_3", LocalDateTime.of(2020, 4, 4, 4, 4, 4), duration_10m, 1));
        task_4 = manager.createNewTask(new Task(
                "Task_4", LocalDateTime.of(2020, 5, 5, 5, 5, 5), duration_10m));
        epic_5 = manager.createNewEpic(new Epic("Epic_5"));
        subtask_6 = manager.createNewSubtask(new Subtask(
                "Subtask_6", LocalDateTime.of(2020, 6, 6, 6, 6, 6), duration_10m, 5));

        t_0 = null;
        s_2 = null;
        e_1 = null;

        task_List = new ArrayList<>(10);
        subtask_List = new ArrayList<>(10);
        int_List = new ArrayList<>(10);
    }

    @AfterEach
    void afterEach() {
        manager.clearTasks();
        manager.clearEpics();
    }

    @Test
    void shouldNotChangeAnyTask_WhenMethodsCreateNew_HaveValidArgs() {
        Epic epic = manager.createNewEpic(new Epic("Epic_5"));
        assertTrue(isTasksFullyEquals(task_0, new Task("Task_0", start_1, duration_10m)));
        assertTrue(isTasksFullyEquals(epic, new Epic("Epic_5", epic.getId())));
        assertTrue(isTasksFullyEquals(subtask_6, new Subtask("Subtask_6", Status.NEW, subtask_6.getId(), start_1, duration_10m, 5)));
    }

    @Test
    void shouldInitializeAllValues_WhenMethodsCreateNew_DoNotHaveValidArgs() {
        task_0 = manager.createNewTask(new Task(null, null, 0, null, null));
        epic_1 = manager.createNewEpic(new Epic(null, 0));
        subtask_2 = manager.createNewSubtask(new Subtask(null, null, 0, null, null, 1));

        assertFalse(
                isTaskHaveAnyNullValues(task_0) &&
                        isTaskHaveAnyNullValues(epic_1) &&
                        isTaskHaveAnyNullValues(subtask_2));
    }

    @Test
    void shouldUpDateTaskAndSubtaskFieldsAndBeEqual_WhenMethodUpDate_EitherData() {
        upDateTask_t_0();
        upDateSubtask_s_2();
        assertFalse(this.isTasksFullyEquals(task_0, t_0));
        assertFalse(this.isTasksFullyEquals(subtask_2, s_2));
        assertEquals(task_0, t_0);
        assertEquals(subtask_2, s_2);

        upDateEpic_e_1();
        assertFalse(this.isTasksFullyEquals(epic_1, e_1));

        assertEquals(task_0, t_0);
        assertEquals(subtask_2, s_2);
        assertEquals(epic_1, e_1);
    }

    @Test
    void shouldUpDateEitherTasksFieldsAndBeEqual_WhenMethodUpDate_NullData() {
        t_0 = manager.upDateTask(new Task(null, null, 0, null, null));
        s_2 = manager.upDateSubtask(new Subtask(null, null, 2, null, null, 1));

        assertEquals(task_0, t_0);
        assertEquals(subtask_2, s_2);

        e_1 = manager.upDateEpic(new Epic(null, 1));
        assertEquals(epic_1, e_1);
    }

    @Test
    void shouldSetEpicStatusNEW_WhenEpicSetStatusMethod_AllSubtasksNEW() {
        assertEquals(Status.NEW, epic_1.getStatus());
        assertEquals(Status.NEW, epic_5.getStatus());
    }

    @Test
    void shouldSetEpicStatusINPROGRESS_WhenEpicSetStatusMethod_DifferentSubtasksStatus() {
        manager.upDateSubtask(new Subtask("Subtask", Status.DONE, subtask_2.getId(), epic_1.getId()));
        manager.upDateSubtask(new Subtask("Subtask", Status.IN_PROGRESS, subtask_6.getId(), epic_5.getId()));
        assertEquals(Status.IN_PROGRESS, epic_1.getStatus());
        assertEquals(Status.IN_PROGRESS, epic_5.getStatus());
    }

    @Test
    void shouldSetEpicStatusDONE_WhenEpicSetStatusMethod_AllSubtasksDONE() {
        manager.upDateSubtask(new Subtask("Subtask", Status.DONE, subtask_2.getId(), epic_1.getId()));
        manager.upDateSubtask(new Subtask("Subtask", Status.DONE, subtask_3.getId(), epic_1.getId()));
        manager.upDateSubtask(new Subtask("Subtask", Status.DONE, subtask_6.getId(), epic_5.getId()));
        assertEquals(Status.DONE, epic_1.getStatus());
        assertEquals(Status.DONE, epic_5.getStatus());
    }

    @Test
    void shouldReturnRightListTask_WhenGetByIdentifierMethod_AfterCreated() {
        t_0 = manager.getTaskByIdentifier(0);
        e_1 = manager.getEpicByIdentifier(1);
        s_2 = manager.getSubtaskByIdentifier(2);

        assertTrue(this.isTasksFullyEquals(task_0, t_0) &&
                this.isTasksFullyEquals(epic_1, e_1) &&
                this.isTasksFullyEquals(subtask_2, s_2));
    }

    @Test
    void shouldReturnZeroMeaningOfEpicSubtasksListAfterUpDateEpic_WhenMethodEpicGetSubtasksList_1Epic2Subtasks() {
        int i = epic_1.getSubtasksList().size();
        manager.upDateEpic(new Epic("Epic", epic_1.getId()));
        int j = epic_1.getSubtasksList().size();

        assertNotEquals(i, j);
    }

    @Test
    void shouldReturnRightTask_WhenGetByIdentifierMethod_AfterUpDated() {
        upDateTask_t_0();
        upDateSubtask_s_2();
        this.isTasksFullyEquals(t_0, manager.getTaskByIdentifier(0));
        this.isTasksFullyEquals(s_2, manager.getSubtaskByIdentifier(2));
        upDateEpic_e_1();
        this.isTasksFullyEquals(e_1, manager.getEpicByIdentifier(1));
    }

    @Test
    void shouldReturnRightListEpicSubtask_WhenGetEpicSubtaskMethod_Created1Epic2Subtask() {
        List<Subtask> sub_List = manager.getEpicSubtasks(1);
        int_List = epic_1.getSubtasksList();

        for (int i : int_List) {
            subtask_List.add(manager.getSubtaskByIdentifier(i));
        }

        assertEquals(sub_List, subtask_List);
    }

    @Test
    void shouldHaveSubtaskIdInEpicList_WhenEpicGetSubtaskListMethod_Created2Task2Epic3Subtask() {
        task_List = manager.getListTasks();
        task_List.addAll(manager.getListEpics());
        task_List.addAll(manager.getListSubtasks());

        for (Task t : task_List) {
            int_List.add(t.getId());
        }

        int_List.sort(Comparator.naturalOrder());
        assertEquals(int_List, List.of(0, 1, 2, 3, 4, 5, 6));
    }

    @Test
    void shouldSaveTheSameDataInHistoryManager_WhenGetHistoryMethod_AfterGetByID() {
        task_List.add(manager.getTaskByIdentifier(task_0.getId()));
        task_List.add(manager.getSubtaskByIdentifier(subtask_2.getId()));

        List<Task> fromHistory = manager.getHistory().reversed();
        assertEquals(task_List, fromHistory, "данные изменились");
    }

    @Test
    void shouldDeleteDataFromHistoryManager_WhenClearMethod() {
        int i = manager.getHistory().size();
        manager.getSubtaskByIdentifier(2);
        manager.getSubtaskByIdentifier(3);

        assertNotEquals(i, manager.getHistory().size());

        manager.clearEpics();
        manager.clearTasks();

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldHaveOnlyUniqueTasksInHistoryManager_WhenHistoryManagerIsEmpty() {
        int i = manager.getHistory().size();
        manager.getTaskByIdentifier(0);
        manager.getTaskByIdentifier(0);
        manager.getTaskByIdentifier(0);
        int j = manager.getHistory().size();
        assertEquals((i == 0), (j == 1));
    }

    @Test
    void shouldChangePositionTasksInHistory_NotAddEpic_() {
        manager.getTaskByIdentifier(0);
        manager.getSubtaskByIdentifier(2);
        manager.getSubtaskByIdentifier(3);
        task_List = manager.getHistory();

        manager.getTaskByIdentifier(0);
        manager.getEpicByIdentifier(1);
        manager.getSubtaskByIdentifier(3);

        manager.getHistory();
        assertEquals((task_List.size()), (manager.getHistory().size()));
        assertNotEquals((task_List), (manager.getHistory()));
    }

    @Test
    void shouldDeleteFromHistory_FromBeginningToEnd() {
        manager.getTaskByIdentifier(0);
        manager.getSubtaskByIdentifier(2);
        manager.getSubtaskByIdentifier(3);

        int i = manager.getHistory().size();
        boolean start = manager.removeTaskByIdentifier(0);
        int j = manager.getHistory().size();
        boolean mid = manager.removeSubtaskByIdentifier(2);
        int k = manager.getHistory().size();
        boolean end = manager.removeSubtaskByIdentifier(3);

        assertEquals(List.of(i, j, k, manager.getHistory().size()), List.of(3, 2, 1, 0));
    }


}