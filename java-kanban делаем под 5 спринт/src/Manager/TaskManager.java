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

    HashMap<Integer, Task> clearTasks();

    HashMap<Integer, Epic> clearEpics();

    HashMap<Integer, Subtask> clearSubtasks();

    Task getTaskByIdentifier(int identifier);

   // Task getTaskByIdentifier(int identifier, HistoryManager history);

    Epic getEpicByIdentifier(int identifier);

    //Epic getEpicByIdentifier(int identifier, HistoryManager history);

    Subtask getSubtaskByIdentifier(int identifier);

    //Subtask getSubtaskByIdentifier(int identifier, HistoryManager history);

    Task removeTaskByIdentifier(int identifier);

    Epic removeEpicByIdentifier(int identifier);

    Subtask removeSubtaskByIdentifier(Integer identifier);

    Task createNewTasks(Task task);

    Task upDateTask(Task task);

    Epic createNewEpic(Epic epic);

    Epic upDateEpic(Epic epic);

    Subtask createNewSubtask(Subtask subtask);

    Subtask upDateSubtask(Subtask subtask);

    ArrayList<Subtask> getEpicSubtasks(int epicID);

    int count();

    void setNewStatusForEpic(Epic epic);

    List<Task> getHistory();

    <T extends Task> void setHistory(T task);
}
