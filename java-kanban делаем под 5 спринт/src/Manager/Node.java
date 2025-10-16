package Manager;

import Tasks.Task;

public class Node{

    private final Task value;
    private Node previous;
    private Node next;

    public Node(Node previous, Task value, Node next) {
        this.previous = previous;
        this.value = value;
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Task getValue() {
        return value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
