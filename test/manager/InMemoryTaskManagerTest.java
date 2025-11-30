package manager;

import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return Managers.getDefault();
    }

    @BeforeEach
    void beforeEach() {
        super.beforeEach();
    }

    @Test
    void shouldReturnTreeSetListIfTasksHaveCorrectLocalDateTime() {
        TreeSet<Task> set = manager.getPrioritizedTasks();
        assertEquals(5, set.size());
    }

    @Test
    void shouldRightlyCountsEpicEndTime_When3Subtasks() {
        Subtask lastSub = manager.createNewSubtask(new Subtask(
                "Subtask_6", LocalDateTime.of(2021, 6, 6, 6, 6, 6), duration_10m, 1));
        LocalDateTime time = subtask_2.getEndTime().plus(subtask_3.getDuration().plus(lastSub.getDuration()));

        assertEquals(time, epic_1.getEndTime());
    }

    @Test
    void shouldNotAddTasksWithEqualStartTime_When5diffStartTimesAnd3Equals() {
        TreeSet<Task> set = manager.getPrioritizedTasks();
        subtask_List = manager.getListSubtasks();

        manager.createNewTask(task_0);
        manager.createNewSubtask(subtask_2);
        manager.createNewSubtask(subtask_3);

        TreeSet<Task> theSame = manager.getPrioritizedTasks();
        List<Subtask> list = manager.getListSubtasks();

        assertEquals(set, theSame);
        assertNotEquals(list, subtask_List);
    }

    @Test
    void shouldAddUpDatedTaskIfStartTimeUnique_When3EqualStartTimesGetUnique() {
        TreeSet<Task> set = manager.getPrioritizedTasks();
        subtask_List = manager.getListSubtasks();

        t_0 = manager.createNewTask(task_0);
        s_2 = manager.createNewSubtask(subtask_2);
        Subtask s_3 = manager.createNewSubtask(subtask_3);

        manager.upDateTask(new Task("task_0", Status.DONE, t_0.getId()));
        manager.upDateSubtask(new Subtask(
                "Subtask_2", Status.DONE, s_2.getId(), LocalDateTime.of(2021, 3, 3, 3, 3, 3), duration_10m, 1));
        manager.upDateSubtask(new Subtask(
                "Subtask_3", Status.DONE, s_3.getId(), LocalDateTime.of(2021, 4, 4, 4, 4, 4), duration_10m, 1));

        TreeSet<Task> theSame = manager.getPrioritizedTasks();
        assertEquals(set, theSame);
    }


}