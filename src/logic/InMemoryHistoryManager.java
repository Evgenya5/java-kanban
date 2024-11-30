package logic;

import data.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    ArrayList<Task> taskHistory = new ArrayList<>();
    private static final int historyMaxSize = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (taskHistory.size() == historyMaxSize) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
