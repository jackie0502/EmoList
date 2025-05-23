package com.app.emolist.Controller;

import com.app.emolist.DataBase.TaskRepository;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> taskList;

    public TaskManager() {taskList = TaskRepository.loadTasks();
    }

    public List<Task> getAllTasks() {return taskList;}

    public void addTask(Task task) {
        taskList.add(task);
        TaskRepository.saveTasks(taskList);
    }

    public void removeTask(Task task) {
        taskList.remove(task);
        TaskRepository.saveTasks(taskList);
    }

    public void updateTask(int index, Task newTask) {
        if (index >= 0 && index < taskList.size()) {
            taskList.set(index, newTask);
            TaskRepository.saveTasks(taskList);
        }
    }

    public void saveAll() {
        TaskRepository.saveTasks(taskList);
    }

    public void reload() {
        taskList = TaskRepository.loadTasks();
    }
}
