package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    Task createNewTask(Task task);

    Epic createNewEpic(Epic epic);

    Subtask createNewSubtask(Subtask subtask);

    Task upDateTask(Task task);

    Epic upDateEpic(Epic epic);

    Subtask upDateSubtask(Subtask subtask);

    Task getTaskByIdentifier(int identifier);

    Epic getEpicByIdentifier(int identifier);

    Subtask getSubtaskByIdentifier(int identifier);

    ArrayList<Subtask> getEpicSubtasks(int epicID);

    ArrayList<Task> getListTasks();

    ArrayList<Epic> getListEpics();

    ArrayList<Subtask> getListSubtasks();

    boolean removeTaskByIdentifier(int identifier);

    boolean removeEpicByIdentifier(int identifier);

    boolean removeSubtaskByIdentifier(int identifier);

    boolean clearTasks();

    boolean clearEpics();

    boolean clearSubtasks();

    List<Task> getHistory();

    <T extends Task> void setHistory(T task);
}
