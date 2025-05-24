package com.app.emolist.Controller;

import com.app.emolist.DataBase.TaskRepository;
import java.util.Comparator;
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

    // 依截止日期升冪排序
    public void sortByDeadDate() {
        taskList.sort(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    // 依優先順序排序（假設 getPriority() 回傳 int，數字越小優先）
    public void sortByPriority() {
        taskList.sort(Comparator.comparingInt(Task::getPriority));
    }

    // 依任務名稱排序
    public void sortByTitle() {
        taskList.sort(Comparator.comparing(Task::getTitle));
    }
}
