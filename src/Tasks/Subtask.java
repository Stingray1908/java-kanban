package Tasks;

public class Subtask extends Task {

    private int epicKey;

    public Subtask(String name, int epicKey) {
        super(name);
        this.epicKey = epicKey;
    }

    public Subtask(String name, Status status, int epicKey, int id) {
        super(name, status, id);
        this.epicKey = epicKey;
        this.setDescription();
    }

    public int getEpicKey() {
        return epicKey;
    }

    public void setEpicKey(int epicKey) {
        if (!(epicKey == this.getId())) {
            this.epicKey = epicKey;
        }
    }

    public String toString() {
        return super.toString() + ", id эпика - " + epicKey;
    }
}

