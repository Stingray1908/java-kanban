package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> mapTasks = new HashMap<>();
    private final HashMap<Integer, Epic> mapEpics = new HashMap<>();
    private final HashMap<Integer, Subtask> mapSubtasks = new HashMap<>();
    private final TreeSet<Task> taskSet = new TreeSet<>(Comparator.comparing(Task::getStartTime)); //orderedTasks

    private int counter = 0;

    @Override
    public Task createNewTask(Task task) {
        if (task != null) {
            int id = count();
            task.setId(id);
            task.setStatus(Status.NEW);
            addToTaskSet(task);
            mapTasks.put(id, task);
        }
        return task;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        if (epic != null) {
            int id = count();
            epic.setId(id);
            mapEpics.put(id, epic);
        }
        return epic;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = count();
            subtask.setId(id);
            Epic epic;
            if ((epic = mapEpics.get(subtask.getEpicKey())) == null) return null;
            ArrayList<Integer> subtasksList = epic.getSubtasksList();
            subtasksList.add(id);
            mapSubtasks.put(id, subtask);
            setNewStatusForEpic(epic);
            setTimeForEpic(epic);
            addToTaskSet(subtask);
        }
        return subtask;
    }

    @Override
    public Task upDateTask(Task upTask) {
        if (upTask != null) {
            int id = upTask.getId();
            Task oldTask;
            if ((oldTask = mapTasks.get(id)) == null) return null;
            taskSet.remove(oldTask);
            addToTaskSet(upTask);
            mapTasks.put(id, upTask);
        }
        return upTask;
    }

    @Override
    public Epic upDateEpic(Epic upEpic) {
        if (upEpic != null) {
            int id = upEpic.getId();
            if (!removeEpicByIdentifier(id)) return null;
            mapEpics.put(id, upEpic);
        }
        return upEpic;
    }

    @Override
    public Subtask upDateSubtask(Subtask upSub) {
        if (upSub != null) {
            Epic epic;
            Subtask oldSubtask;
            int epicID = upSub.getEpicKey();
            int newSubID = upSub.getId();

            if ((epic = mapEpics.get(epicID)) == null) return null;
            if ((oldSubtask = mapSubtasks.get(newSubID)) == null) return null;

            taskSet.remove(oldSubtask);
            mapSubtasks.put(newSubID, upSub);
            setNewStatusForEpic(epic);
            setTimeForEpic(epic);
            addToTaskSet(upSub);
        }
        return upSub;
    }

    @Override
    public Task getTaskByIdentifier(int id) {
        Task task;
        if ((task = mapTasks.get(id)) != null) setHistory(task);
        return task;
    }

    @Override
    public Epic getEpicByIdentifier(int id) {
        return mapEpics.get(id);
    }

    @Override
    public Subtask getSubtaskByIdentifier(int id) {
        Subtask subtask;
        if ((subtask = mapSubtasks.get(id)) != null) setHistory(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicID) {
        Epic epic;
        if ((epic = mapEpics.get(epicID)) == null) return null;
        return epic.getSubtasksList().stream()
                .map(mapSubtasks::get)
                .filter(s -> epicID == s.getEpicKey())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Task> getListTasks() {
        return new ArrayList<>(mapTasks.values());
    }

    @Override
    public ArrayList<Epic> getListEpics() {
        return new ArrayList<>(mapEpics.values());
    }

    @Override
    public ArrayList<Subtask> getListSubtasks() {
        return new ArrayList<>(mapSubtasks.values());
    }

    @Override
    public boolean removeTaskByIdentifier(int id) {
        if (mapTasks.remove(id) != null) {
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEpicByIdentifier(int id) {
        for (Subtask s : getEpicSubtasks(id)) {
            removeSubtaskByIdentifier(s.getId());
        }
        historyManager.remove(id);
        mapEpics.remove(id);
        return true;
    }

    @Override
    public boolean removeSubtaskByIdentifier(int id) {
        Subtask subtask;
        Epic epic;
        if ((subtask = mapSubtasks.get(id)) == null) return false;
        int epicKey = subtask.getEpicKey();

        if ((epic = mapEpics.get(epicKey)) == null) return false;
        ArrayList<Integer> keyList = epic.getSubtasksList();

        if (keyList.isEmpty()) return false;
        keyList.remove((Integer) id);
        taskSet.remove(subtask);
        historyManager.remove(id);
        mapSubtasks.remove(id);
        setNewStatusForEpic(epic);
        setTimeForEpic(epic);
        return true;
    }

    @Override
    public boolean clearTasks() {
        new ArrayList<>(mapTasks.keySet())
                .forEach(this::removeTaskByIdentifier);
        return mapTasks.isEmpty();
    }

    @Override
    public boolean clearEpics() {
        if (clearSubtasks()) {
            new ArrayList<>(mapEpics.keySet())
                    .forEach(this::removeEpicByIdentifier);
        }
        return mapEpics.isEmpty();
    }

    @Override
    public boolean clearSubtasks() {
        if (mapEpics.isEmpty() && !mapSubtasks.isEmpty()) return false;
        new ArrayList<>(mapEpics.keySet()).stream()
                .map(this::getEpicSubtasks)
                .flatMap(List::stream)
                .forEach(s -> removeSubtaskByIdentifier(s.getId()));
        return mapSubtasks.isEmpty();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public <T extends Task> void setHistory(T task) {
        if (task != null) historyManager.add(task);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return taskSet;
    }

    protected void setCounter(int countFrom) {
        counter = countFrom;
    }

    private int count() {
        return counter++;
    }

    private void setNewStatusForEpic(Epic epic) {
        ArrayList<Subtask> subsList = getEpicSubtasks(epic.getId());
        if (subsList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Subtask sub = subsList.getFirst();
        Status status = sub.getStatus();

        for (Subtask s : subsList) {
            if (s.getStatus() != status) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(status);
    }

    private void setTimeForEpic(Epic epic) {
        ArrayList<Subtask> al = getEpicSubtasks(epic.getId());

        if (al.isEmpty()) return;
        Optional<LocalDateTime> opt = al.stream()
                .map(Task::getStartTime)
                .filter(l -> l != Task.DEFAULT_DATE_TIME)
                .min(LocalDateTime::compareTo);

        if (opt.isEmpty()) return;
        Duration duration = al.stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(opt.get());
        epic.setDuration(duration);
    }

    private boolean isTimeCrossingTwoTasks(Task t1, Task t2) {
        if (t1.getStartTime().isAfter(t2.getEndTime()) ||
                t2.getStartTime().isAfter(t1.getEndTime())) return false;
        return true;
    }

    private boolean isTimeCrossingTaskToTreeSet(Task task) {
        return taskSet.stream()
                .anyMatch(t1 -> isTimeCrossingTwoTasks(task, t1));
    }

    private void addToTaskSet(Task task) {
        if (task.getStartTime() == Task.DEFAULT_DATE_TIME ||
                isTimeCrossingTaskToTreeSet(task)) return;
        taskSet.add(task);
    }
}