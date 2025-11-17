package manager;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(Path path) throws IOException {
        return new FileBackedTaskManager(path);
    }


}
