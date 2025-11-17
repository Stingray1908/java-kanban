import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] arg) throws IOException /*throws IOException*/ {
        Path path = Files.createTempFile("Temp_File", ".csv");
        InMemoryTaskManager manager = new InMemoryTaskManager();

        manager.createNewTask(new Task("убрать вещи", null, null));
        manager.createNewTask(new Task("постирать бельё", null, null));

        manager.createNewEpic(new Epic("Переезд"));
        manager.createNewEpic(new Epic("Стройка"));

        manager.createNewSubtask(new Subtask("собрать вещи", null, null, 2));
        manager.createNewSubtask(new Subtask("купить билет", null, null, 2));
        manager.createNewSubtask(new Subtask("залить фундамент", null, null, 3));

        manager.upDateSubtask(new Subtask("собрать вещи", Status.DONE, 2, null, null, 4));

        manager.removeTaskByIdentifier(0);
        manager.removeTaskByIdentifier(1);

        manager.createNewTask(new Task("убрать вещи", null, null));
        manager.createNewTask(new Task("постирать бельё", null, null));

        System.out.println(manager.getListEpics());
        System.out.println(manager.getListSubtasks());

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            List<String> list = new ArrayList<>();
            String str;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            for (String s : list) {
                System.out.println(s);
            }
        }

        System.out.println("_________________________________________________________________________________");

        manager = FileBackedTaskManager.loadFromFile(path);

        manager.createNewTask(new Task("сделать ТЗ 7", null, null));
        manager.createNewTask(new Task("в этом году", null, null));

        manager.createNewTask(new Task("сделать ТЗ 8", null, null));
        manager.createNewTask(new Task("в новом году", null, null));

        List<Task> listT = manager.getListTasks();
        listT.addAll(manager.getListEpics());
        listT.addAll(manager.getListSubtasks());
        System.out.println("'''''''''''''''''''''''");
        System.out.println(manager.getPrioritizedTasks());
    }
}
