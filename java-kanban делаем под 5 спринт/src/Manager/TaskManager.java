package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getListTasks();

    ArrayList<Epic> getListEpics();

    ArrayList<Subtask> getListSubtasks();

    boolean clearTasks();

    boolean clearEpics();

    boolean clearSubtasks();

    Task getTaskByIdentifier(int identifier);

    Epic getEpicByIdentifier(int identifier);

    Subtask getSubtaskByIdentifier(int identifier);

    boolean removeTaskByIdentifier(int identifier);

    boolean removeEpicByIdentifier(int identifier);

    boolean removeSubtaskByIdentifier(Integer identifier);

    Task createNewTasks(Task task);

    Task upDateTask(Task task);

    Epic createNewEpic(Epic epic);

    Epic upDateEpic(Epic epic);

    Subtask createNewSubtask(Subtask subtask);

    Subtask upDateSubtask(Subtask subtask);

    ArrayList<Subtask> getEpicSubtasks(int epicID);

    List<Task> getHistory();

    <T extends Task> void setHistory(T task);
}
