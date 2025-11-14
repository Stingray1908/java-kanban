package tasks;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task {
    // сделать конструктор который просто меняет статус


    private final String name;
    private Status status;
    private int id;
    private String description;

    public static final DateTimeFormatter START_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("'['yyyy.MM.dd-HH:mm:ss']'");
    public static final  LocalDateTime DEFAULT_DATE_TIME =
            LocalDateTime.of(1, 1, 1, 0, 0, 0);

    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name) {
        this(name, Status.NEW, 0, null, null);
    }

    public Task(String name, LocalDateTime startTime, Duration duration) {
        this(name, Status.NEW, 0, startTime, duration);
    }

    public Task(String name, Status status, int id) {
        this(name, status, id, null, null);
    }

    public Task(String name, Status status, int id, LocalDateTime startTime, Duration duration) {
        this.name = (name == null) ? "" : name;
        this.status = (status == null) ? Status.NEW : status;
        this.id = id;
        this.startTime = (startTime == null) ? DEFAULT_DATE_TIME : startTime;
        this.duration = (duration == null) ? Duration.ZERO : duration;
        setDescription();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (object == this) return true;
        if ((object.getClass()) != getClass()) return false;
        Task anotherTask = (Task) object;
        return Objects.equals(id, anotherTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String statusTask = "null";
        if (status == Status.NEW) statusTask = "NEW";
        if (status == Status.DONE) statusTask = "DONE";
        if (status == Status.IN_PROGRESS) statusTask = "IN_PROGRESS";

        String start = startTime.format(START_TIME_FORMATTER);
        Long minutes = duration.toMinutes();

        return String.format("\nID:%d, %s, %s, %s, мин:%d", id, name, statusTask, start, minutes);
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        setDescription();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > -1) this.id = id;
        setDescription();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDescription() {
        this.description = toString();
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plus(this.duration);
    }

    protected void setStartTime(LocalDateTime startTime) {
        if (startTime == null) return;
        if (this.startTime == DEFAULT_DATE_TIME) this.startTime = startTime;
        if (startTime.isBefore(this.startTime)) this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        if (duration != null) this.duration = duration;
    }

}
