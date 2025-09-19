package Tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksList = new ArrayList<>();

    public Epic(String name) {
        super(name);
    }

    public Epic(String name, int id) {
        super(name);
        this.setId(id);    //поставить статус
    }

    public ArrayList<Integer> getSubtasksList() {
        return subtasksList;
    }

    public void setSubtasksList(ArrayList<Integer> subtasksList) {
        this.subtasksList = subtasksList;
        this.setDescription();
    }

    public String toString() {
        int listLength;
        if (subtasksList != null) {
            listLength = subtasksList.size();
        } else {
            listLength = 0;
        }
        return super.toString() + ", количество подзадач - " + listLength;
    }
}



