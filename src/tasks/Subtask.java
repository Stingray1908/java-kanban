package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicKey;

    public Subtask(String name, int epicKey) {
        this(name, Status.NEW, 0, null, null, epicKey);
    }

    public Subtask(String name, LocalDateTime startTime, Duration duration, int epicKey) {
        this(name, Status.NEW, 0, startTime, duration, epicKey);
    }

    public Subtask(String name, Status status, int id, int epicKey) {
        this(name, status, id, null, null, epicKey);
    }

    public Subtask(String name, Status status, int id, LocalDateTime startTime, Duration duration, int epicKey) {
        super(name, status, id, startTime, duration);
        setEpicKey(epicKey);
        setDescription();
    }

    public int getEpicKey() {
        return epicKey;
    }

    public void setEpicKey(int epicKey) {
        if (epicKey != this.getId()) this.epicKey = epicKey;
    }

    public String toString() {
        return super.toString() + String.format(", epic:%d", epicKey);
    }
}

