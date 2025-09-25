package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public  class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
    }

    private HashMap<Integer, Task> mapTasks = new HashMap<>();
    private HashMap<Integer, Epic> mapEpics = new HashMap<>();
    private HashMap<Integer, Subtask> mapSubtasks = new HashMap<>();


    private int counter = 0;

    @Override
    public ArrayList<Task> getListTasks() {
        if (!(mapTasks.isEmpty())) {
            return new ArrayList<>(mapTasks.values());
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Epic> getListEpics() {
        if (!(mapEpics.isEmpty())) {
            return new ArrayList<>(mapEpics.values());
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Subtask> getListSubtasks() {
        if (!(mapSubtasks.isEmpty())) {
            return new ArrayList<>(mapSubtasks.values());
        }
        return new ArrayList<>();
    }

    @Override
    public HashMap<Integer, Task> clearTasks() {
        if (!(mapTasks.isEmpty())) {
            mapTasks.clear();
        }
        return mapTasks;
    }

    @Override
    public HashMap<Integer, Epic> clearEpics() {
        if (!(mapEpics.isEmpty())) {
            if (!(mapSubtasks.isEmpty())) {
                clearSubtasks();
            }
            if (mapSubtasks.isEmpty()) {
                mapEpics.clear();
            }
        }
        return mapEpics;
    }

    @Override
    public HashMap<Integer, Subtask> clearSubtasks() {
        if (!(mapEpics.isEmpty())) {
            if (!(mapSubtasks.isEmpty())) {
                for (Integer id : mapEpics.keySet()) {
                    ArrayList<Subtask> subtasks = getEpicSubtasks(id);
                    if (!(subtasks.isEmpty())) {
                        for (Subtask subtask : subtasks) {
                            removeSubtaskByIdentifier(subtask.getId());
                        }
                    }
                }
            }
        }
        return mapSubtasks;
    }


    @Override
    public Task getTaskByIdentifier(int identifier) {
        if (!(mapTasks.isEmpty()) && mapTasks.containsKey(identifier)) {
            Task task = mapTasks.get(identifier);
            setHistory(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicByIdentifier(int identifier) {
        if (!(mapEpics.isEmpty()) && mapEpics.containsKey(identifier)) {
            Epic epic = mapEpics.get(identifier);
            setHistory(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskByIdentifier(int identifier) {
        if (!(mapSubtasks.isEmpty()) && mapSubtasks.containsKey(identifier)) {
            Subtask subtask = mapSubtasks.get(identifier);
            setHistory(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public Task removeTaskByIdentifier(int identifier) {
        if (!(mapTasks.isEmpty()) && mapTasks.containsKey(identifier)) {
            mapTasks.remove(identifier);
        }
        return null;
    }

    @Override
    public Epic removeEpicByIdentifier(int identifier) {
        if (!(mapEpics.isEmpty()) && (mapEpics.containsKey(identifier))) {
            ArrayList<Subtask> listSubtasks = getEpicSubtasks(identifier);
            if (!(listSubtasks.isEmpty()) && (!(mapSubtasks.isEmpty()))) {
                for (Subtask subtask : listSubtasks) {
                    int id = subtask.getId();
                    removeSubtaskByIdentifier(id);
                }
            }
            mapEpics.remove(identifier);
        }
        return null;
    }

    @Override
    public Subtask removeSubtaskByIdentifier(Integer identifier) {
        if (!(mapEpics.isEmpty()) && (!(mapSubtasks.isEmpty()) && (mapSubtasks.containsKey(identifier)))) {
            Subtask subtask = mapSubtasks.get(identifier);
            int epicKey = subtask.getEpicKey();
            if (mapEpics.containsKey(epicKey)) {
                Epic epic = mapEpics.get(epicKey);
                ArrayList<Integer> listKeys = epic.getSubtasksList();
                if ((!listKeys.isEmpty()) && (listKeys.contains(identifier))) {
                    listKeys.remove(identifier);
                    epic.setSubtasksList(listKeys);
                    setNewStatusForEpic(epic);
                    mapEpics.put(epicKey, epic);
                    mapSubtasks.remove(identifier);
                }
            }
        }
        return null;
    }


    @Override
    public Task createNewTasks(Task task) {
        int id = count();
        if (mapTasks.isEmpty() || (!mapTasks.containsKey(id))) {
            task.setId(id);
            mapTasks.put(id, task);
            return task;
        }
        return null;
    }

    @Override
    public Task upDateTask(Task task) {
        int id = task.getId();
        if ((id <= counter) && (!(mapTasks.isEmpty()) && mapTasks.containsKey(id))) {
            mapTasks.put(id, task);
        }
        return null;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        int id = count();
        if (mapEpics.isEmpty() || (!(mapEpics.containsKey(id)))) {
            epic.setId(id);
            mapEpics.put(id, epic);
            return epic;
        }
        return null;
    }

    @Override
    public Epic upDateEpic(Epic epic) {
        int id = epic.getId();
        if ((id <= counter) && (!(mapEpics.isEmpty()) && (mapEpics.containsKey(id)))) {
            removeEpicByIdentifier(id);
            mapEpics.put(id, epic);
        }
        return null;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        int id = count();
        int epicKey = subtask.getEpicKey();
        if (!(mapEpics.isEmpty()) && mapEpics.containsKey(epicKey)) {
            if (((mapSubtasks.isEmpty()) || (!(mapSubtasks.containsKey(id))))) {
                subtask.setId(id);
                Epic epic = mapEpics.get(epicKey);
                ArrayList<Integer> subtasksList = epic.getSubtasksList();
                if ((subtasksList.isEmpty()) || (!(subtasksList.contains(id)))) {
                    subtasksList.add(id);
                    epic.setSubtasksList(subtasksList);
                    setNewStatusForEpic(epic);
                    mapEpics.put(epicKey, epic);
                    mapSubtasks.put(id, subtask);
                    return subtask;
                }
            }
        }
        return null;
    }

    @Override
    public Subtask upDateSubtask(Subtask subtask) {
        if (!(mapEpics.isEmpty()) && (!(mapSubtasks.isEmpty()))) {
            int iD = subtask.getId();
            int epicKey = subtask.getEpicKey();
            if (((mapEpics.containsKey(epicKey)) && (mapSubtasks.containsKey(iD)))) {
                ArrayList<Subtask> listSubtasks = getEpicSubtasks(epicKey);
                if (!(listSubtasks.isEmpty()) && listSubtasks.contains(subtask)) {
                    mapSubtasks.put(iD, subtask);
                    Epic epic = mapEpics.get(epicKey);
                    setNewStatusForEpic(epic);
                    mapEpics.put(epicKey, epic);
                    return subtask;
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicID) {
        if (!(mapSubtasks.isEmpty()) && (!(mapEpics.isEmpty())) && (mapEpics.containsKey(epicID))) {
            ArrayList<Subtask> listSubtasks = new ArrayList<>();
            Epic anotherEpic = mapEpics.get(epicID);
            ArrayList<Integer> keysFromEpic = anotherEpic.getSubtasksList();
            if (!(keysFromEpic.isEmpty())) {
                for (Integer subID : keysFromEpic) {
                    if (mapSubtasks.containsKey(subID)) {
                        Subtask subtask = mapSubtasks.get(subID);
                        if (epicID == subtask.getEpicKey()) {
                            listSubtasks.add(subtask);
                        }
                    }
                }
                return listSubtasks;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public <T extends Task> void setHistory(T task) {
        historyManager.add(task);
    }

    private int count() {
        return counter++;
    }

    private void setNewStatusForEpic(Epic epic) {
        ArrayList<Subtask> listID = getEpicSubtasks(epic.getId());
        Status lastStatus;
        if (!(listID.isEmpty())) {
            Subtask subtask = listID.getLast();
            lastStatus = subtask.getStatus();
            if (listID.size() != 1) {
                for (Subtask aSubtask : listID) {
                    Status newStatus = aSubtask.getStatus();
                    if (lastStatus != newStatus) {
                        epic.setStatus(Status.IN_PROGRESS);
                        mapEpics.put(epic.getId(), epic);
                        return;
                    }
                }
            }
            epic.setStatus(lastStatus);
        } else {
            epic.setStatus(Status.NEW);
        }
        mapEpics.put(epic.getId(), epic);
    }
}