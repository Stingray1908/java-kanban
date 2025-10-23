package manager;

import tasks.Task;

import java.util.List;
import java.util.ArrayList;

public interface HistoryManager {

    <T extends Task> void add(T task);

    List<Task> getHistory();
}
