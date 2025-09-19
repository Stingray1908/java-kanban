import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

public class Main {
    public static void main(String[] arg) {

        TaskManager manager = new TaskManager();

        manager.createNewTasks(new Task("убрать вещи"));
        manager.createNewTasks(new Task("постирать бельё"));
        //manager.getListTasks();
        manager.createNewEpic(new Epic("Переезд"));
        manager.createNewEpic(new Epic("Стройка"));
        // manager.getListEpics();
        manager.createNewSubtask(new Subtask("собрать вещи", 2));
        manager.createNewSubtask(new Subtask("купить билет", 2));
        manager.createNewSubtask(new Subtask("залить фундамент",3));
        //
        //System.out.println(manager.getListEpics());
        //System.out.println(manager.getListSubtasks());
        //System.out.println(manager.getListTasks());
//
        //   manager.upDateTask(new Task("убрать вещи", Status.IN_PROGRESS, 0));
        // manager.upDateTask(new Task("постирать бельё", Status.DONE, 1));
        // manager.getListTasks();
        manager.upDateSubtask(new Subtask("собрать вещи", Status.DONE    , 2, 4));
        manager.upDateSubtask(new Subtask("купить билет", Status.DONE, 2, 5));
        manager.upDateSubtask(new Subtask("залить фундамент", Status.DONE, 3, 6));





        // manager.removeTaskByIdentifier(0);
        //  manager.removeSubtaskByIdentifier(4);
        //  manager.removeEpicByIdentifier(2);

        //  manager.clearEpics();
//  manager.clearSubtasks();
        //    manager.getListEpics();

        // manager.clearTasks();                                              //Исправить ошибки!!!!
        //manager.clearSubtasks();
        //manager.clearEpics();

        System.out.println(manager.getListEpics());
        //    System.out.println(manager.getEpicSubtasks(2));
        System.out.println(manager.getListSubtasks());
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


    }
}
