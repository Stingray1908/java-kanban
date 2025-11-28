package manager;

import exceptions.NotAcceptableException;
import tasks.*;
import exceptions.NotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> mapTasks = new HashMap<>();
    private final HashMap<Integer, Epic> mapEpics = new HashMap<>();
    private final HashMap<Integer, Subtask> mapSubtasks = new HashMap<>();
    private final TreeSet<Task> taskSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private int counter = 0;

    @Override
    public Task createNewTask(Task task) {
        if (task != null) {
            task.setId(-1);
            if (isTimeCrossingTaskToTreeSet(task))
                throw new NotAcceptableException("TaskCrossingTreeSet when createNewTask");
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
            subtask.setId(-1);
            if (isTimeCrossingTaskToTreeSet(subtask))
                throw new NotAcceptableException("TaskCrossingTreeSet when createNewSubtask");
            int id = count();
            subtask.setId(id);
            Epic epic;
            if ((epic = mapEpics.get(subtask.getEpicKey())) != null) {
                ArrayList<Integer> subtasksList = epic.getSubtasksList();
                subtasksList.add(id);
                mapSubtasks.put(id, subtask);
                addToTaskSet(subtask);
                setNewStatusForEpic(epic);
                setTimeForEpic(epic);
                return subtask;
            }
        }
        throw new NotFoundException();
    }

    @Override
    public Task upDateTask(Task upTask) {
        if (upTask == null) throw new NotFoundException("UpTask = null");
        int id = upTask.getId();
        if (!mapTasks.containsKey(id)) throw new NotFoundException("Not Task with id:" + id);
        if (isTimeCrossingTaskToTreeSet(upTask))
            throw new NotAcceptableException("TaskCrossingTreeSet when upDateTask");
        addToTaskSet(upTask);
        mapTasks.put(id, upTask);
        return upTask;
    }

    @Override
    public Epic upDateEpic(Epic upEpic) {
        if (upEpic == null) throw new NotFoundException("upEpic = null");
        int id = upEpic.getId();
        if (!mapEpics.containsKey(id)) throw new NotFoundException("Not Task with id:" + id);
        removeEpicByIdentifier(id);
        mapEpics.put(id, upEpic);
        return upEpic;
    }

    @Override
    public Subtask upDateSubtask(Subtask upSub) {
        if (upSub == null) throw new NotFoundException("upSubtask = null");
        Epic epic;
        int epicID = upSub.getEpicKey();
        int newSubID = upSub.getId();

        if ((epic = mapEpics.get(epicID)) == null)
            throw new NotFoundException("upDateSubtask, Not Epic with id:" + newSubID);

        if (!mapSubtasks.containsKey(newSubID))
            throw new NotFoundException("upDateSubtask, Not Subtask with id:" + newSubID);

        if (isTimeCrossingTaskToTreeSet(upSub))
            throw new NotAcceptableException("TimeCrossingTaskToTreeSet when upDateSubtask");

        addToTaskSet(upSub);
        mapSubtasks.put(newSubID, upSub);
        setNewStatusForEpic(epic);
        setTimeForEpic(epic);
        return upSub;
    }

    @Override
    public Task getTaskByIdentifier(int id) {
        Task task;
        if ((task = mapTasks.get(id)) == null) throw new NotFoundException("when getTaskById:" + id);
        setHistory(task);
        return task;
    }

    @Override
    public Epic getEpicByIdentifier(int id) {
        Epic epic;
        if ((epic = mapEpics.get(id)) == null) throw new NotFoundException("when getEpicById:" + id);
        return epic;
    }

    @Override
    public Subtask getSubtaskByIdentifier(int id) {
        Subtask subtask;
        if ((subtask = mapSubtasks.get(id)) == null) throw new NotFoundException("when getSubtaskById:" + id);
        setHistory(subtask);
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
        Task t;
        if ((t = mapTasks.remove(id)) != null) {
            taskSet.remove(t);
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
        if (epic.getSubtasksList().isEmpty()) return;

        LocalDateTime startTime = Task.DEFAULT_DATE_TIME;
        Duration duration = Duration.ZERO;
        LocalDateTime endTime = Task.DEFAULT_DATE_TIME;

        for (Subtask s : getEpicSubtasks(epic.getId())) {
            LocalDateTime start = s.getStartTime();

            if (start.equals(Task.DEFAULT_DATE_TIME)) continue;
            if (startTime.equals(Task.DEFAULT_DATE_TIME) || start.isBefore(startTime)) startTime = start;

            LocalDateTime end = s.getEndTime();
            if (end.isAfter(endTime)) endTime = end;
            duration = duration.plus(s.getDuration());
        }
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    private boolean isTimeCrossingTwoTasks(Task t1, Task t2) {
        return !t1.getStartTime().isAfter(t2.getEndTime()) &&
                !t2.getStartTime().isAfter(t1.getEndTime());
    }

    private boolean isTimeCrossingTaskToTreeSet(Task task) {
        for (Task t : taskSet) {
            if (isTimeCrossingTwoTasks(t, task) && !t.equals(task)) return true;
        }
        return false;
    }

    private void addToTaskSet(Task task) {
        taskSet.add(task);
    }
}