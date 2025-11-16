package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksList = new ArrayList<>();
    private LocalDateTime endTime = Task.DEFAULT_DATE_TIME;

    public Epic(String name) {
        this(name, 0);
    }

    public Epic(String name, int id) {
        super(name, Status.NEW, id, null, null);
    }

    public ArrayList<Integer> getSubtasksList() {
        return subtasksList;
    }

    public void setSubtasksList(ArrayList<Integer> list) {
        if (list != null && !list.contains(this.getId())) this.subtasksList = list;
        setDescription();
    }

    public String toString() {
        int list = 0;
        if (subtasksList != null) list = subtasksList.size();
        return super.toString() + String.format(", sub:%d", list);
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null || endTime.isBefore(DEFAULT_DATE_TIME)) return;
        this.endTime = endTime;
    }
}



