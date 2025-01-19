package logic;

import data.Task;
import java.util.*;

class Node {
    public Task data;
    public Node next;
    public Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> taskHistoryHashMap = new HashMap<>();
    private Node head; //Указатель на первый элемент списка. Он же first
    private Node tail; //Указатель на последний элемент списка. Он же last

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(taskHistoryHashMap.get(task.getId()));
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(taskHistoryHashMap.get(id));
    }

    private void linkLast(Task task) {

        final Node newNode = new Node(tail, task, null);

        if (tail == null)
            head = newNode;
        else
            tail.next = newNode;
        tail = newNode;
        taskHistoryHashMap.put(task.getId(),newNode);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> nodeTasks = new ArrayList<>();
        Node nextNode = head;
        while (nextNode != null) {
            nodeTasks.add(nextNode.data);
            nextNode = taskHistoryHashMap.get(nextNode.data.getId()).next;
        }
        return nodeTasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node currentPrevNode = node.prev;
        Node currentNextNode = node.next;
        taskHistoryHashMap.remove(node.data.getId());
        if (currentNextNode != null) {
            currentNextNode.prev = currentPrevNode;
        } else {
            tail = currentPrevNode;
        }
        if (currentPrevNode != null) {
            currentPrevNode.next = currentNextNode;
        } else {
            head = currentNextNode;
        }
    }
}
