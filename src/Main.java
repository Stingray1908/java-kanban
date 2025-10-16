import Manager.InMemoryTaskManager;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import java.util.List;

public class Main {
    public static void main(String[] arg) {

        TaskManager manager = new InMemoryTaskManager();

        manager.createNewTasks(new Task("убрать вещи"));
        manager.createNewTasks(new Task("постирать бельё"));
        //manager.getListTasks();
        manager.createNewEpic(new Epic("Переезд"));
        manager.createNewEpic(new Epic("Стройка"));
        // manager.getListEpics();
        manager.createNewSubtask(new Subtask("собрать вещи", 2));
        manager.createNewSubtask(new Subtask("купить билет", 2));
        manager.createNewSubtask(new Subtask("залить фундамент", 3));
        //
        //System.out.println(manager.getListEpics());
        //System.out.println(manager.getListSubtasks());
        //System.out.println(manager.getListTasks());
//
        //   manager.upDateTask(new Task("убрать вещи", Status.IN_PROGRESS, 0));
        // manager.upDateTask(new Task("постирать бельё", Status.DONE, 1));
        // manager.getListTasks();
        //manager.upDateSubtask(new Subtask("собрать вещи", Status.DONE, 2, 4));
        //System.out.println(manager.getListEpics());
        //manager.upDateSubtask(new Subtask("купить билет", Status.DONE, 2, 5));
        //System.out.println(manager.getListEpics());
       // manager.upDateSubtask(new Subtask("залить фундамент", Status.DONE, 3, 6));

        manager.getTaskByIdentifier(0);
        manager.getTaskByIdentifier(1);//1
        manager.getEpicByIdentifier(2);//2
        manager.getEpicByIdentifier(3);
        manager.getTaskByIdentifier(0);
        manager.getEpicByIdentifier(3);//3
        manager.getEpicByIdentifier(4);//4
        manager.getEpicByIdentifier(7);  //7
        manager.getEpicByIdentifier(0);   //0




        List<Task> list = manager.getHistory();
        for(Task task : list) {
            System.out.println(task);
        }


        //System.out.println(manager.getListEpics());
        /*System.out.println(manager.getTaskByIdentifier(0));
        System.out.println(manager.getTaskByIdentifier(1));
        System.out.println(manager.getEpicByIdentifier(2));
        System.out.println(manager.getEpicByIdentifier(3));
        System.out.println(manager.getSubtaskByIdentifier(4));
        System.out.println(manager.getSubtaskByIdentifier(5));
        System.out.println(manager.getSubtaskByIdentifier(6));
        System.out.println(manager.getSubtaskByIdentifier(7));
        System.out.println(manager.getSubtaskByIdentifier(8));
        System.out.println(manager.getHistory());*/


        // manager.removeTaskByIdentifier(0);
        //  manager.removeSubtaskByIdentifier(4);
        //  manager.removeEpicByIdentifier(2);

        //  manager.clearEpics();
//  manager.clearSubtasks();
        //    manager.getListEpics();

        // manager.clearTasks();                                              //Исправить ошибки!!!!
        //manager.clearSubtasks();
        //manager.clearEpics();

        //System.out.println(manager.getListEpics());
        //    System.out.println(manager.getEpicSubtasks(2));
        // System.out.println(manager.getListSubtasks());
        // System.out.println(manager.getListTasks());

        //manager.getListTasks();
        //manager.getListSubtasks();
        // System.out.println(manager.removeEpicByIdentifier(40));


        //manager.upDateEpic(new Epic("Заезд", 2));


        //     manager.getListSubtasks();
        //   manager.getListEpics();

        //  manager.createNewSubtask(new Subtask("распаковать вещи", 2));
        //manager.createNewSubtask(new Subtask("найти ближайшую Пятёрочку", 2));

        //manager.getListSubtasks();
        //manager.getListEpics();
        //     manager.getEpicSubtasks(2);

    /*    TaskManager taskManager = Managers.getDefault();

       taskManager.createNewEpic(new Epic("Собака"));
        taskManager.createNewSubtask(new Subtask("Выгулить", 0));
        taskManager.createNewSubtask(new Subtask("Накормить", 0));

        taskManager.createNewEpic(new Epic("Кошка"));
        taskManager.createNewSubtask(new Subtask("Вычесать", 3));
        taskManager.createNewSubtask(new Subtask("Стричь когти", 3));

        taskManager.createNewTasks(new Task("протереть пыль"));
        taskManager.createNewTasks(new Task("помыть полы"));

         taskManager.getEpicByIdentifier(0);
        taskManager.getEpicByIdentifier(3);
        taskManager.getSubtaskByIdentifier(1);
        taskManager.getSubtaskByIdentifier(2);
        taskManager.getSubtaskByIdentifier(4);
        taskManager.getSubtaskByIdentifier(5);

      // printAllTasks(taskManager);
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getListTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getListEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getListSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }*/


    }
}
