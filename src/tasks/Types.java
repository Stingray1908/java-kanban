package tasks;

public enum Types {
    TASK,
    EPIC,
    SUBTASK;

    public static Types getType(Task task) throws IllegalArgumentException {

        return switch (task) {
            case null -> throw new IllegalArgumentException();
            case Subtask ignored -> SUBTASK;
            case Epic ignored -> EPIC;
            default -> TASK;
        };
    }
}


