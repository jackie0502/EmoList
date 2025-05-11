package com.app.emolist.DataBase;

import com.app.emolist.Controller.Task;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private static final String FILE_PATH = "tasks.json";

    public void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                writer.write("  {\n");
                writer.write("    \"title\": \"" + escape(task.getTitle()) + "\",\n");
                writer.write("    \"completed\": " + task.isCompleted() + ",\n");
                writer.write("    \"deadline\": \"" + task.getDeadline().toString() + "\"\n");
                writer.write("  }" + (i < tasks.size() - 1 ? "," : "") + "\n");
            }
            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return tasks;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String title = null;
            boolean completed = false;
            LocalDate deadline = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\"title\"")) {
                    title = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                } else if (line.startsWith("\"completed\"")) {
                    completed = Boolean.parseBoolean(line.split(":")[1].trim().replace(",", ""));
                } else if (line.startsWith("\"deadline\"")) {
                    String dateStr = line.split(":")[1].trim().replaceAll("^\"|\"$", "");
                    deadline = LocalDate.parse(dateStr);
                    // 一筆資料完成後加入
                    if (title != null && deadline != null) {
                        Task task = new Task(title, deadline);
                        if (completed) task.toggleCompleted(); // 若已完成則切換
                        tasks.add(task);
                        title = null; // reset
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}
