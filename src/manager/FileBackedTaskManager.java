package manager;

import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import tasks.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path fileToSave;

    public FileBackedTaskManager(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            Path dir = path.getParent();
            if (!Files.exists(dir)) Files.createDirectory(dir);
            Files.createFile(path);
        }
        fileToSave = path;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        Epic parent = super.createNewEpic(epic);
        save();
        return parent;
    }

    @Override
    public Task createNewTask(Task task) {
        Task parent = super.createNewTask(task);
        save();
        return parent;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        Subtask parent = super.createNewSubtask(subtask);
        save();
        return parent;
    }

    @Override
    public Task upDateTask(Task task) {
        Task parent = super.upDateTask(task);
        save();
        return parent;
    }

    @Override
    public Epic upDateEpic(Epic epic) {
        Epic parent = super.upDateEpic(epic);
        save();
        return parent;
    }

    @Override
    public Subtask upDateSubtask(Subtask subtask) {
        Subtask parent = super.upDateSubtask(subtask);
        save();
        return parent;
    }

    @Override
    public boolean removeTaskByIdentifier(int identifier) {
        boolean parent = super.removeTaskByIdentifier(identifier);
        save();
        return parent;
    }

    @Override
    public boolean removeEpicByIdentifier(int identifier) {
        boolean parent = super.removeEpicByIdentifier(identifier);
        save();
        return parent;
    }

    @Override
    public boolean removeSubtaskByIdentifier(int identifier) {
        boolean parent = super.removeSubtaskByIdentifier(identifier);
        save();
        return parent;
    }

    private void save() {
        List<Task> list = getListTasks();
        list.addAll(getListEpics());
        list.addAll(getListSubtasks());

        String hat = "id,type,name,status,features(epicID or SubtaskQuantity)";

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(fileToSave.toFile(), false))) {
            bw.write(hat);

            for (Task task : list) {
                bw.newLine();
                bw.append(taskToString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Проблемы с сохранением данных", e);
        }
    }

    private String taskToString(Task task) {

        int id = task.getId();
        Types type = Types.getType(task);
        String name = task.getName();
        Status status = task.getStatus();
        String startTime = task.getStartTime().format(Task.START_TIME_FORMATTER);
        long duration = task.getDuration().toMinutes();

        String string = String.format("%d,%s,%s,%s,%s,%d", id, type, name, status, startTime, duration);

        switch (type) {
            case EPIC:
                Epic epic = (Epic) task;
                string += "," + epic.getSubtasksList().size();
                break;
            case SUBTASK:
                Subtask sub = (Subtask) task;
                string += "," + sub.getEpicKey();
                break;
            default:
                break;
        }
        return string;
    }

    public Task fromString(String line) {
        String[] features = line.split(",");
        int id = Integer.parseInt(features[0]);
        Types type;
        String name = features[2];
        Task task;

        if (features[1].equals("TASK")) {
            type = Types.TASK;
        } else if (features[1].equals("SUBTASK")) {
            type = Types.SUBTASK;
        } else {
            return new Epic(name, id);
        }

        Status status = Status.NEW;
        LocalDateTime startTime = LocalDateTime.parse(features[4], Task.START_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(Integer.parseInt(features[5]));

        if (features[3].equals("DONE")) {
            status = Status.DONE;
        } else if (features[3].equals("IN_PROGRESS")) {
            status = Status.IN_PROGRESS;
        }

        if (type == Types.TASK) {
            task = new Task(name, status, id, startTime, duration);
        } else {
            int idEpic = Integer.parseInt(features[6]);
            task = new Subtask(name, status, id, startTime, duration, idEpic);
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(Path path) throws IOException {
        FileBackedTaskManager manager = Managers.getFileBackedTaskManager(path);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            br.readLine();
            String line;
            int maxID = 0;
            while ((line = br.readLine()) != null) {
                Task task = manager.fromString(line);
                int id = task.getId();
                manager.setCounter(id);

                if (id > maxID) maxID = id;
                if (task instanceof Epic) {
                    manager.createNewEpic((Epic) task);
                    continue;
                }

                Status status = task.getStatus();
                Task anotherTask;
                if (task instanceof Subtask) {
                    anotherTask = manager.createNewSubtask((Subtask) task);
                } else {
                    anotherTask = manager.createNewTask(task);
                }
                if (status != Status.NEW) anotherTask.setStatus(status);
            }
            manager.setCounter(maxID + 1);
        }
        return manager;
    }
}
