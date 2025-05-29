package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    @FXML
    private ComboBox<String> rangeComboBox;

    private TaskManager taskManager;

    @FXML
    private void initialize() {
        pieChart.setTitle("任務完成率");
        lineChart.setTitle("壓力分數趨勢");
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
                String color;
                if (data.getName().equals("已完成")) {
                    color = "#228B22";  // 綠色
                } else {
                    color = "#D0021B";  // 紅色
                }

                data.getNode().setStyle("-fx-pie-color: " + color + ";");

                // 設定 legend 文字前面的小圓形顏色
                Node node = data.getNode();
                if (node != null && node.getParent() != null) {
                    for (Node legend : pieChart.lookupAll(".chart-legend-item")) {
                        if (legend instanceof Label label && label.getText().equals(data.getName())) {
                            Node symbol = label.getGraphic(); // 通常是小圓形
                            if (symbol != null) {
                                symbol.setStyle("-fx-background-color: " + color + ";");
                            }
                        }
                    }
                }
            }
        });


        // 壓力圖（LineChart）資料處理
        Map<LocalDate, Integer> weeklyPressureMap = new TreeMap<>();

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(25);
        LocalDate to = today.plusDays(5);

// 統計每週壓力總和
        for (Task t : tasks) {
            LocalDate date = t.getDeadline();
            if (date == null || date.isBefore(from) || date.isAfter(to)) continue;

            // 對應到該週的週一
            LocalDate weekStart = date.with(java.time.DayOfWeek.MONDAY);

            weeklyPressureMap.compute(weekStart, (d, v) -> (v == null ? 0 : v) + t.getStressLevel());
        }

// 建立折線圖資料（每週一筆）
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("每週壓力總和");

        for (Map.Entry<LocalDate, Integer> entry : weeklyPressureMap.entrySet()) {
            LocalDate weekStart = entry.getKey();
            int weeklySum = entry.getValue();
            series.getData().add(new XYChart.Data<>(weekStart.toString(), weeklySum));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

}
