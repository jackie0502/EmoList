package com.app.emolist;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> taskList = new ArrayList<>();

    public void addTask(Task task) {
        taskList.add(task);
    }

    public List<Task> getTasks() {
        return taskList;
    }
}
