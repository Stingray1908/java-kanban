package tasks;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    static Task task1;
    static Task task2;

    @Test
    void taskShouldBeEqualIfTheSameId() {
        task1 = new Task("Task", Status.NEW, 0);
        task2 = new Task("Task", Status.NEW, 0);
        assertEquals(task1, task2, "false");
    }

    @Test
    void subtaskShouldBeEqualIfTheSameId () {
        task1 = new Subtask("Subtask", Status.NEW, 1, 2);
        task2 = new Subtask("Subtask", Status.NEW, 1, 2);
        assertEquals(task1, task2, "false");
        }

    @Test
    void epicShouldBeEqualIfTheSameId() {
        task1 = new Epic("Epic", 1);
        task2 = new Epic("Epic", 1);
        assertEquals(task1, task2, "false");
    }
}