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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsPanelController {
    @FXML private HBox view;
    @FXML private PieChart pieChart;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private ComboBox<String> rangeComboBox;

    @FXML private ComboBox<String> viewRangeChoice;

    private TaskManager taskManager;

    @FXML
    private void initialize() {
        pieChart.setTitle("任務完成率");
        lineChart.setTitle("壓力分數趨勢");

        CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setVisible(false);

        lineChart.setLegendVisible(true);
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
        lineChart.getData().clear();

        LocalDate today = LocalDate.now();
        LocalDate from ;
        LocalDate to ;

        String selectedRange = viewRangeChoice.getValue(); // 讀取 ComboBox 選項
        if (selectedRange == null) return;

        switch (selectedRange) {
            case "每日":
                from = today.minusDays(6);
                to = today;
                Map<LocalDate, Integer> dailyPressureMap = new TreeMap<>();
                for (Task t : tasks) {
                    LocalDate date = t.getDeadline();
                    if (date == null || date.isBefore(from) || date.isAfter(to)) continue;
                    dailyPressureMap.compute(date, (d, v) -> (v == null ? 0 : v) + t.getStressLevel());
                }
                // 補齊空日
                for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                    dailyPressureMap.putIfAbsent(d, 0);
                }
                applyYAxisScale(dailyPressureMap);

                XYChart.Series<String, Number> dailySeries = new XYChart.Series<>();
                dailySeries.setName("每日壓力總和");
                for (Map.Entry<LocalDate, Integer> entry : dailyPressureMap.entrySet()) {
                    LocalDate date = entry.getKey();
                    int dailySum = entry.getValue();
                    dailySeries.getData().add(new XYChart.Data<>(date.toString(), dailySum));
                }
                lineChart.getData().clear();
                lineChart.getData().add(dailySeries);
                break;
            case "每週":
                from = today.minusWeeks(5).with(DayOfWeek.MONDAY);
                to = today;
                // 統計每週壓力總和
                Map<LocalDate, Integer> weeklyPressureMap = new TreeMap<>();
                for (Task t : tasks) {
                    LocalDate date = t.getDeadline();
                    if (date == null || date.isBefore(from) || date.isAfter(to)) continue;
                    // 對應到該週的週一
                    LocalDate weekStart = date.with(java.time.DayOfWeek.MONDAY);
                    weeklyPressureMap.compute(weekStart, (d, v) -> (v == null ? 0 : v) + t.getStressLevel());
                }
                // 補齊每週（週一為 key）
                for (LocalDate d = from.with(java.time.DayOfWeek.MONDAY); !d.isAfter(to); d = d.plusWeeks(1)) {
                    weeklyPressureMap.putIfAbsent(d, 0);
                }
                applyYAxisScale(weeklyPressureMap);

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
                break;
            case "每月":
                from = today.minusMonths(11).withDayOfMonth(1);
                to = today;
                Map<LocalDate, Integer> monthlyPressureMap = new TreeMap<>();
                for (Task t : tasks) {
                    LocalDate date = t.getDeadline();
                    if (date == null || date.isBefore(from) || date.isAfter(to)) continue;

                    LocalDate monthStart = date.withDayOfMonth(1);
                    monthlyPressureMap.compute(monthStart, (d, v) -> (v == null ? 0 : v) + t.getStressLevel());
                }
                // 補齊每月（每月 1 號為 key）
                for (LocalDate d = from.withDayOfMonth(1); !d.isAfter(to); d = d.plusMonths(1)) {
                    monthlyPressureMap.putIfAbsent(d, 0);
                }
                applyYAxisScale(monthlyPressureMap);

                XYChart.Series<String, Number> monthlySeries = new XYChart.Series<>();
                monthlySeries.setName("每月壓力總和");
                for (Map.Entry<LocalDate, Integer> entry : monthlyPressureMap.entrySet()) {
                    LocalDate monthStart = entry.getKey();
                    int monthlySum = entry.getValue();
                    monthlySeries.getData().add(new XYChart.Data<>(monthStart.toString(), monthlySum));
                }
                lineChart.getData().clear();
                lineChart.getData().add(monthlySeries);
                break;
        }
    }
    private void applyYAxisScale(Map<LocalDate, Integer> dataMap) {
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        int max = dataMap.values().stream().max(Integer::compareTo).orElse(10);

        double upper = Math.ceil(max / 10.0) * 10; // 向上取整至10的倍數
        if (upper == 0) upper = 10; // 防止為零

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(upper);
        yAxis.setTickUnit(upper / 5); // 5 格為一單位（可視需求調整）
    }

}
