package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksList = new ArrayList<>();

    public Epic(String name) {
        this(name, 0);
    }

    public Epic(String name, int id) {
        super(name, Status.NEW, id, null, null);
    }

    public ArrayList<Integer> getSubtasksList() {
        return subtasksList;
    }

    public void setSubtasksList(ArrayList<Integer> subtasksList) {
        if (!subtasksList.contains(this.getId())) this.subtasksList = subtasksList;
        setDescription();
    }

    public String toString() {
        int list = 0;
        if (subtasksList != null) list = subtasksList.size();
        return super.toString() + String.format(", sub:%d", list);
    }

    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
    }

}



