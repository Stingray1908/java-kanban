package tasks;

import java.util.Objects;

public class Task {


    private final String name;
    private Status status;
    private int id;
    private String description;

    public Task(String name) {
        this.name = name;
        this.status = Status.NEW;
    }

    public Task(String name, Status status, int id) {
        this.name = name;
        this.status = status;
        this.id = id;
        this.description = toString();

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
        String statusTask;
        String aName;
        if (name == null) {
            aName = "null";
        }
        if (status == null) {
            statusTask = "null";
        } else if (status == Status.NEW) {
            statusTask = "NEW";
        } else if (status == Status.DONE) {
            statusTask = "DONE";
        } else {
            statusTask = "IN_PROGRESS";
        }
        return String.format("\nID: %d, \"%s\", %s", id, name, statusTask);
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.description = toString();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (!(id < 0)) {
            this.id = id;
            this.description = toString();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription() {
        this.description = toString();
    }
}
