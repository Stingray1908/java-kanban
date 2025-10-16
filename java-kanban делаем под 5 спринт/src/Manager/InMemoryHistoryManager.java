package Manager;

import Tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public <T extends Task> void add(T task) {
        if (task == null) return;
        int id = task.getId();
        remove(id);
        Node currentTail = tail;
        Node endNode = new Node(currentTail, task, null);

        if (head == null) {
            head = endNode;

        } else {
            currentTail.setNext(endNode);
        }

        tail = endNode;
        history.put(id, endNode);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node node = history.get(id);

            if (head == node && node == tail) {
                head = null;
                tail = null;

            } else {
                Node previous = node.getPrevious();
                Node next = node.getNext();

                if (previous == null) {
                    next.setPrevious(null);
                    head = next;
                }

                if (next == null) {
                    previous.setNext(null);
                    tail = previous;
                }

                if (next != null && previous != null) {
                    previous.setNext(next);
                    next.setPrevious(previous);
                }
            }
            history.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyArr = new ArrayList<>();

        if (!history.isEmpty()) {
            Node node = tail;

            while(node != null) {
                historyArr.add(node.getValue());
                node = node.getPrevious();
            }
        }
        return historyArr;
    }
}
