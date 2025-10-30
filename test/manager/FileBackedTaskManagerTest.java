package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

class FileBackedTaskManagerTest {

    public static Path path;
    public static BufferedWriter writer;
    public static BufferedReader reader;
    public static FileBackedTaskManager manager;

    @BeforeAll
    static void beforeAll() {
        try {
            path = Files.createTempFile("TestFile", ".csv");
        } catch (IOException e) {
            System.out.println("beforeAll error");
        }
    }

    @BeforeEach
    void beforeEach() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            writer = new BufferedWriter(new FileWriter(path.toFile(), false));
            reader = new BufferedReader(new FileReader(path.toFile()));
            manager = Managers.getFileBackedTaskManager(path);
        } catch (IOException e) {
            System.out.println("beforeEach error");
        } finally {
            manager.createNewTasks(new Task("goal_№_1"));
            Epic epic = manager.createNewEpic(new Epic("goal_№_2"));
            manager.createNewSubtask(new Subtask("goal_№_3", epic.getId()));
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("resource close error");
        }
    }

    public boolean isFileAndArrLinesEquals(String[] lineAll) throws IOException {
        reader.readLine();
        String line;

        for (String s : lineAll) {
            line = reader.readLine();
            boolean equals = Objects.equals(line, s);
            if (!equals) return false;
        }
        return true;
    }

    public boolean isHashMapsBothOfManagersEquals(FileBackedTaskManager a, FileBackedTaskManager b) {
        ArrayList<Task> arrA = new ArrayList<>(a.getListTasks());
        arrA.addAll(a.getListEpics());
        arrA.addAll(a.getListSubtasks());

        ArrayList<Task> arrB = new ArrayList<>(b.getListTasks());
        arrB.addAll(b.getListEpics());
        arrB.addAll(b.getListSubtasks());

        return arrA.equals(arrB);
    }

    @Test
    void assertManagerCorrectlySavesTasksToFile() throws IOException {
        Files.readString(path);
        String[] arr = {"0,TASK,goal_№_1,NEW", "1,EPIC,goal_№_2,NEW,1", "2,SUBTASK,goal_№_3,NEW,1"};
        assertTrue(isFileAndArrLinesEquals(arr));
    }

    @Test
    void assertManagerCorrectlyUpDatesSavesToFile() throws IOException {
        manager.upDateTask(new Task("aim_№_1", Status.IN_PROGRESS, 0));
        Epic epic = manager.getEpicByIdentifier(1);
        manager.upDateSubtask(new Subtask("aim_№_3", Status.IN_PROGRESS, epic.getId(), 2));

        String[] arr = {"0,TASK,aim_№_1,IN_PROGRESS", "1,EPIC,goal_№_2,IN_PROGRESS,1", "2,SUBTASK,aim_№_3,IN_PROGRESS,1"};
        assertTrue(isFileAndArrLinesEquals(arr));
    }

    @Test
    void assertManagerCorrectlyDeleteDatesFromFile() throws IOException {
        assertFalse(isFileAndArrLinesEquals(new String[1]));

        manager.clearTasks();
        manager.clearEpics();
        reader.close();
        reader = new BufferedReader(new FileReader(path.toFile()));

        assertTrue(isFileAndArrLinesEquals(new String[1]));
    }

    @Test
    void assertMethodLoadFromFileOfTaskManagerUsingFromStringMethodIsWorking() throws IOException {
        FileBackedTaskManager restored = FileBackedTaskManager.loadFromFile(path);
        assertTrue(isHashMapsBothOfManagersEquals(manager, restored));
    }

    @Test
    void assertFileBackedFileManagerAbleToSaveAndRestoreEmptyFile() throws IOException {
        Path TestFile_2 = Files.createTempFile("TestFile_2", "csv");
        manager = Managers.getFileBackedTaskManager(TestFile_2);
        FileBackedTaskManager restored = FileBackedTaskManager.loadFromFile(TestFile_2);
        assertTrue(isHashMapsBothOfManagersEquals(manager, restored));
        Files.deleteIfExists(TestFile_2);
    }

    @Test
    void assertTheSameFileLoadsAfter100Cycles() throws IOException {
        Path TestFile_2 = Files.createTempFile("TestFile_2", "csv");
        manager = new FileBackedTaskManager(TestFile_2);
        int counter = 0;
        while (counter != 33) {
            String name = String.format("number %d", counter);
            manager.createNewTasks(new Task(name));
            Epic epic = manager.createNewEpic(new Epic(name));
            manager.createNewSubtask(new Subtask(name, epic.getId()));
            if (counter == 31) {
                manager.clearTasks();
                manager.clearEpics();
            }
            counter++;
        }
        FileBackedTaskManager restored = FileBackedTaskManager.loadFromFile(TestFile_2);
        assertTrue(isHashMapsBothOfManagersEquals(manager, restored));
        Files.deleteIfExists(TestFile_2);
    }
}