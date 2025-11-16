package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager = Managers.getDefault();
    Epic epic = taskManager.createNewEpic(new Epic("Epic_1"));

    @Test
    void shouldDoNotHaveNewCreatedSubtaskWithWrongId() {
        Subtask subtask = new Subtask("(Epic)_Subtask", Status.NEW, epic.getId(), epic.getId());
        assertTrue(epic.getSubtasksList().isEmpty());
    }

    @Test
    void ShouldDoNotHaveEpicInEpicSubtasksList() {
        ArrayList<Integer> testList = new ArrayList<>();
        testList.add(epic.getId());
        epic.setSubtasksList(testList);
        assertEquals(0, epic.getSubtasksList().size());
    }

    @Test
    void shouldDoNotHaveNullPointerExceptionWhenToStringWithEpicNameNull() {
        Epic epicTest = new Epic(null);
    }
}