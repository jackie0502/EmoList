package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsPanelController {
    @FXML private HBox view;
    @FXML private PieChart pieChart;
    @FXML private LineChart<String, Number> lineChart;

    private TaskManager taskManager;

    @FXML
    private void initialize() {
        pieChart.setTitle("任務完成率");
        lineChart.setTitle("壓力指數趨勢");
    }

    public void setTaskManager(TaskManager manager) {
        this.taskManager = manager;
        updateCharts();
    }

    public void updateCharts() {
        if (taskManager == null) return;

        List<Task> tasks = taskManager.getAllTasks();
        int total = tasks.size();
        int completedCount = (int) tasks.stream().filter(Task::isCompleted).count();
        int incompleteCount = total - completedCount;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        PieChart.Data completed = new PieChart.Data("已完成", completedCount);
        PieChart.Data incomplete = new PieChart.Data("未完成", incompleteCount);
        pieData.addAll(completed, incomplete);
        pieChart.setData(pieData);

        // 延遲設定顏色，避免節點尚未初始化造成 null
        javafx.application.Platform.runLater(() -> {
            for (PieChart.Data data : pieData) {
                if (data.getName().equals("已完成")) {
                    data.getNode().setStyle("-fx-pie-color: #228B22;");  // 綠色
                } else if (data.getName().equals("未完成")) {
                    data.getNode().setStyle("-fx-pie-color: #D0021B;");  // 紅色
                }
            }
        });

        // 壓力圖（LineChart）資料處理
        // 壓力圖（LineChart）資料處理（限定 ±x 天）
        Map<LocalDate, Integer> pressureMap = new TreeMap<>();
        Map<LocalDate, Integer> taskCountMap = new TreeMap<>();

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(15);
        LocalDate to = today.plusDays(15);

        for (Task t : tasks) {
            LocalDate date = t.getDeadline();
            if (date == null || date.isBefore(from) || date.isAfter(to)) continue;

            pressureMap.put(date, pressureMap.getOrDefault(date, 0) + t.getPriority());
            taskCountMap.put(date, taskCountMap.getOrDefault(date, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("壓力指數（平均）");

        for (Map.Entry<LocalDate, Integer> entry : pressureMap.entrySet()) {
            LocalDate date = entry.getKey();
            int totalPressure = entry.getValue();
            int taskCount = taskCountMap.get(date); // 此處不會為 null
            double averagePressure = (double) totalPressure / taskCount;
            series.getData().add(new XYChart.Data<>(date.toString(), averagePressure));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

}
