package logic;

import data.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{


    private Map<Integer, Node> taskHistoryHashMap = new HashMap<>();
    private Node head; //Указатель на первый элемент списка. Он же first
    private Node tail; //Указатель на последний элемент списка. Он же last

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        taskHistoryHashMap.put(task.getId(),newNode);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> nodeTasks = new ArrayList<>();
        Node nextNode = head;
        while (nextNode != null) {
            nodeTasks.add(nextNode.data);
            nextNode = taskHistoryHashMap.get(nextNode.data.getId()).next;
        }
        return nodeTasks;
    }

    private void removeNode(Node node) {
        Node currentPrevNode = node.prev;
        node.prev = null;
        Node currentNextNode = node.next;
        node.next = null;
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

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (taskHistoryHashMap.get(id) == null) {
            return;
        }
        removeNode(taskHistoryHashMap.get(id));
        taskHistoryHashMap.remove(id);
    }
}
