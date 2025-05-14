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
                writer.write("    \"deadline\": \"" + task.getDeadline().toString() + "\",\n");
                writer.write("    \"category\": \"" + escape(task.getCategory()) + "\",\n");
                writer.write("    \"priority\": " + task.getPriority() + ",\n");
                writer.write("    \"tags\": \"" + escape(task.getTags()) + "\",\n");
                writer.write("    \"recurrence\": \"" + escape(task.getRecurrence()) + "\"\n");
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
            String category = "其他";
            int priority = 1;
            String tags = "";
            String recurrence = "無";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{")) {
                    // 開始讀取一筆任務
                    title = null;
                    completed = false;
                    deadline = null;
                    category = "其他";
                    priority = 1;
                    tags = "";
                    recurrence = "無";
                } else if (line.startsWith("\"title\"")) {
                    title = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                } else if (line.startsWith("\"completed\"")) {
                    completed = Boolean.parseBoolean(line.split(":")[1].trim().replace(",", ""));
                } else if (line.startsWith("\"deadline\"")) {
                    String dateStr = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                    deadline = LocalDate.parse(dateStr);
                } else if (line.startsWith("\"category\"")) {
                    category = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                } else if (line.startsWith("\"priority\"")) {
                    String numStr = line.split(":")[1].trim().replaceAll(",", "");
                    try {
                        priority = Integer.parseInt(numStr);
                    } catch(NumberFormatException e) {
                        priority = 1;
                    }
                } else if (line.startsWith("\"tags\"")) {
                    tags = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                } else if (line.startsWith("\"recurrence\"")) {
                    recurrence = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                } else if (line.startsWith("}")) {
                    // 結束一筆任務，建立 Task 物件
                    if (title != null && deadline != null) {
                        Task task = new Task(title, deadline);
                        if (completed) {
                            task.setCompleted(true);
                        }
                        task.setCategory(category);
                        task.setPriority(priority);
                        task.setTags(tags);
                        task.setRecurrence(recurrence);
                        tasks.add(task);
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
