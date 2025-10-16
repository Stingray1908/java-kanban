package Tasks;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    Subtask subtask_1 = new Subtask("Name", Status.NEW, 0,1);

     @Test
    void shouldHoldSaveEpicKeyAgainstEpicKeyBecomesTheSameSubtaskId() {
        subtask_1.setEpicKey(subtask_1.getId());
        assertEquals(0, subtask_1.getEpicKey());
    }

}