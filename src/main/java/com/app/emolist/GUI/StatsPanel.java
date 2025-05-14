package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsPanel {
    private HBox view = new HBox(20);
    private PieChart pieChart;
    private LineChart<String, Number> lineChart;
    private TaskManager taskManager;

    public StatsPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        pieChart = new PieChart();
        pieChart.setTitle("任務完成率");
        lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        lineChart.setTitle("壓力指數趨勢");
        // 設定座標軸標籤
        ((CategoryAxis) lineChart.getXAxis()).setLabel("日期");
        ((NumberAxis) lineChart.getYAxis()).setLabel("壓力指數");

        pieChart.setPrefSize(300, 300);
        lineChart.setPrefSize(450, 300);

        view.getChildren().addAll(pieChart, lineChart);
        updateCharts();
    }

    public HBox getView() {
        return view;
    }

    public void updateCharts() {
        // 計算完成率（已完成 vs 未完成任務數）
        List<Task> tasks = taskManager.getTasks();
        int total = tasks.size();
        int completedCount = 0;
        for (Task t : tasks) {
            if (t.isCompleted()) {
                completedCount++;
            }
        }
        int incompleteCount = total - completedCount;
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("已完成", completedCount),
                new PieChart.Data("未完成", incompleteCount)
        );
        pieChart.setData(pieData);

        // 計算各日期的壓力指數（按日期彙總任務重要性）
        Map<LocalDate, Integer> pressureMap = new TreeMap<>();
        for (Task t : tasks) {
            LocalDate date = t.getDeadline();
            int value = pressureMap.getOrDefault(date, 0);
            value += t.getPriority();
            pressureMap.put(date, value);
        }
        // 建立折線圖資料序列
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
            // 座標軸與標題文字改為白色
            if(lineChart.getXAxis().lookup(".axis-label") != null) lineChart.getXAxis().lookup(".axis-label").setStyle("-fx-text-fill: #FFFFFF;");
            if(lineChart.getYAxis().lookup(".axis-label") != null) lineChart.getYAxis().lookup(".axis-label").setStyle("-fx-text-fill: #FFFFFF;");
            ((CategoryAxis) lineChart.getXAxis()).setTickLabelFill(Color.WHITE);
            ((NumberAxis) lineChart.getYAxis()).setTickLabelFill(Color.WHITE);
            if(pieChart.lookup(".chart-title") != null) pieChart.lookup(".chart-title").setStyle("-fx-text-fill: #FFFFFF;");
            if(lineChart.lookup(".chart-title") != null) lineChart.lookup(".chart-title").setStyle("-fx-text-fill: #FFFFFF;");
        } else {
            view.setStyle("");
            if(lineChart.getXAxis().lookup(".axis-label") != null) lineChart.getXAxis().lookup(".axis-label").setStyle("-fx-text-fill: #000000;");
            if(lineChart.getYAxis().lookup(".axis-label") != null) lineChart.getYAxis().lookup(".axis-label").setStyle("-fx-text-fill: #000000;");
            ((CategoryAxis) lineChart.getXAxis()).setTickLabelFill(Color.BLACK);
            ((NumberAxis) lineChart.getYAxis()).setTickLabelFill(Color.BLACK);
            if(pieChart.lookup(".chart-title") != null) pieChart.lookup(".chart-title").setStyle("-fx-text-fill: #000000;");
            if(lineChart.lookup(".chart-title") != null) lineChart.lookup(".chart-title").setStyle("-fx-text-fill: #000000;");
        }
    }
}
