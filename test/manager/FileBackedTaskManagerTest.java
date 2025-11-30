package manager;

import static java.util.Arrays.compare;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    public static Path path;
    public static BufferedWriter writer;
    public static BufferedReader reader;

    public FileBackedTaskManager manager;

    public static LocalDateTime start_1;
    public static LocalDateTime start_2;
    public static LocalDateTime start_3;
    public static LocalDateTime start_4;
    public static LocalDateTime start_5;

    public static Duration duration;

    @Override
    public FileBackedTaskManager createManager() {
        try {
            return Managers.getFileBackedTaskManager(path);
        } catch (IOException e) {
            throw new RuntimeException("creating manager error", e);
        }
    }

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
        super.beforeEach();
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            writer = new BufferedWriter(new FileWriter(path.toFile(), false));
            reader = new BufferedReader(new FileReader(path.toFile()));
            manager = createManager();

            start_1 = LocalDateTime.of(1, 1,1, 0, 0, 0);
            start_2 = LocalDateTime.of(2, 2, 2, 0, 0, 0);
            start_3 = LocalDateTime.of(3, 3, 3, 0, 0, 0);
            start_4 = LocalDateTime.of(4, 4, 4, 0, 0, 0);
            start_5 = LocalDateTime.of(5, 5, 5, 0, 0, 0);

            duration = Duration.ofMinutes(1);

            manager.createNewTask(new Task("id_0", start_1, Duration.ofMinutes(5)));
            Epic epic = manager.createNewEpic(new Epic("id_1"));
            manager.createNewSubtask(new Subtask(
                    "id_2",  start_2, Duration.ofMinutes(5), epic.getId()));
        } catch (IOException e) {
            System.out.println("beforeEach error");
        }
    }

    @AfterEach
    void afterEach() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
        } catch (IOException e) {
            System.out.println("resource close error");
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("AfterAll resource close error" + e.getMessage());
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
        arrA.sort(Comparator.comparing(Task::getId));

        ArrayList<Task> arrB = new ArrayList<>(b.getListTasks());
        arrB.addAll(b.getListEpics());
        arrB.addAll(b.getListSubtasks());
        arrB.sort(Comparator.comparing(Task::getId));
        return arrA.equals(arrB);
    }

    @Test
    void assertManagerCorrectlySavesTasksToFile() throws IOException {
        String[] arr = {"0,TASK,id_0,NEW,[0001.01.01-00:00:00],5",
                "1,EPIC,id_1,NEW,[0002.02.02-00:00:00],5,1",
                "2,SUBTASK,id_2,NEW,[0002.02.02-00:00:00],5,1"};
        assertTrue(isFileAndArrLinesEquals(arr));
    }

    @Test
    void assertManagerCorrectlyUpDatesSavesToFile() throws IOException {
        manager.upDateTask(new Task("aim_№_1", Status.IN_PROGRESS, 0, start_3, duration));
        Epic epic = manager.getEpicByIdentifier(1);
        manager.upDateSubtask(new Subtask("aim_№_3", Status.IN_PROGRESS, 2, start_4, duration, epic.getId()));
        String[] arr = {"0,TASK,aim_№_1,IN_PROGRESS,[0003.03.03-00:00:00],1",
                "1,EPIC,id_1,IN_PROGRESS,[0004.04.04-00:00:00],1,1",
                "2,SUBTASK,aim_№_3,IN_PROGRESS,[0004.04.04-00:00:00],1,1"};
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
        LocalDateTime date = LocalDateTime.of(10,1,1,1,1,1);
        while (counter != 33) {

            String name = String.format("number %d", counter);
            date = date.plus(duration_10m);
            manager.createNewTask(new Task(name, date, duration));

            Epic epic = manager.createNewEpic(new Epic(name));
            date = date.plus(duration_10m);
            manager.createNewSubtask(new Subtask(name, date, duration, epic.getId()));
            counter++;
            if (counter == 31) {
                manager.clearTasks();
                manager.clearEpics();
            }
        }
        FileBackedTaskManager restored = FileBackedTaskManager.loadFromFile(TestFile_2);
        assertTrue(isHashMapsBothOfManagersEquals(manager, restored));
        Files.deleteIfExists(TestFile_2);
    }

    @Test
    void shouldThrowManagerSaveException_WhenPathIsNotCorrect() {

        assertThrowsExactly(ManagerSaveException.class, () -> {
            try {
                writer.close();
                writer.write("WDE");
            } catch (IOException e) {
                throw new ManagerSaveException("write err", e);
            }
        });
    }

    @Test
    void shouldThrowManagerSaveException_WhenWritingStringIsNull() {
        assertThrowsExactly(ManagerSaveException.class, () -> {
            String str = null;
            try {
                writer.write(str);
            } catch (RuntimeException e) {
                throw new ManagerSaveException("write err", e);
            }
        });
    }

}