package Tasks;

import Manager.Managers;
import Manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager = Managers.getDefault();
    Epic epic = taskManager.createNewEpic(new Epic("Epic_1"));

    @Test
    void name() {
        Subtask subtask = new Subtask("(Epic)_Subtask", Status.NEW, epic.getId(), epic.getId());
        assertTrue(epic.getSubtasksList().isEmpty());
    }

    @Test
    void name2() {
        ArrayList<Integer> testList = new ArrayList<>();
        testList.add(epic.getId());
        epic.setSubtasksList(testList);
        assertEquals(0, epic.getSubtasksList().size());
    }

}