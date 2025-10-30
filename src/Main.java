import manager.FileBackedTaskManager;
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
    public static void main(String[] arg) throws IOException {
        Path path = Files.createTempFile("Temp_File", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        manager.createNewTasks(new Task("убрать вещи"));
        manager.createNewTasks(new Task("постирать бельё"));

        manager.createNewEpic(new Epic("Переезд"));
        manager.createNewEpic(new Epic("Стройка"));

        manager.createNewSubtask(new Subtask("собрать вещи", 2));
        manager.createNewSubtask(new Subtask("купить билет", 2));
        manager.createNewSubtask(new Subtask("залить фундамент", 3));

        manager.upDateSubtask(new Subtask("собрать вещи", Status.DONE, 2, 4));

        manager.removeTaskByIdentifier(0);
        manager.removeTaskByIdentifier(1);

        manager.createNewTasks(new Task("убрать вещи"));
        manager.createNewTasks(new Task("постирать бельё"));

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

        manager.createNewTasks(new Task("сделать ТЗ 7"));
        manager.createNewTasks(new Task("в этом году"));

        manager.createNewTasks(new Task("сделать ТЗ 8"));
        manager.createNewTasks(new Task("в новом году"));

        List<Task> listT = manager.getListTasks();
        listT.addAll(manager.getListEpics());
        listT.addAll(manager.getListSubtasks());

        System.out.println(listT);


    }
}
