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

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("已完成", completedCount),
                new PieChart.Data("未完成", incompleteCount)
        );
        pieChart.setData(pieData);

        for (PieChart.Data data : pieData) {
            if (data.getName().equals("已完成")) {
                data.getNode().setStyle("-fx-pie-color: #228B22;");  // 原諒的顏色
            } else if (data.getName().equals("未完成")) {
                data.getNode().setStyle("-fx-pie-color: #D0021B;");  // 警示顏色
            }
        }
        Map<LocalDate, Integer> pressureMap = new TreeMap<>();
        for (Task t : tasks) {
            LocalDate date = t.getDeadline();
            pressureMap.put(date, pressureMap.getOrDefault(date, 0) + t.getPriority());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("壓力指數");
        for (Map.Entry<LocalDate, Integer> entry : pressureMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    public void applyTheme(boolean dark) {
        if (dark) {
            view.setStyle("-fx-background-color: #2b2b2b;");
            ((CategoryAxis) lineChart.getXAxis()).setTickLabelFill(Color.WHITE);
            ((NumberAxis) lineChart.getYAxis()).setTickLabelFill(Color.WHITE);
            pieChart.lookup(".chart-title").setStyle("-fx-text-fill: #FFFFFF;");
            lineChart.lookup(".chart-title").setStyle("-fx-text-fill: #FFFFFF;");
        } else {
            view.setStyle("");
            ((CategoryAxis) lineChart.getXAxis()).setTickLabelFill(Color.BLACK);
            ((NumberAxis) lineChart.getYAxis()).setTickLabelFill(Color.BLACK);
            pieChart.lookup(".chart-title").setStyle("-fx-text-fill: #000000;");
            lineChart.lookup(".chart-title").setStyle("-fx-text-fill: #000000;");
        }
    }
}
