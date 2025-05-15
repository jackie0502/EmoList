package com.app.emolist.GUI;

import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import com.app.emolist.GUI.CalendarPanel;
import com.app.emolist.GUI.StatsPanel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskPanel {
    private TaskManager taskManager = new TaskManager();
    private ListView<Task> taskListView = new ListView<>();
    private TextField inputField = new TextField();
    private DatePicker calendar;  // 由 CalendarPanel 傳入
    private TextField searchField = new TextField();
    private ComboBox<String> categoryChoice = new ComboBox<>();
    private ComboBox<String> priorityChoice = new ComboBox<>();
    private ComboBox<String> recurrenceChoice = new ComboBox<>();
    private TextField tagField = new TextField();
    private CheckBox darkModeToggle = new CheckBox("深色模式");

    private VBox view;
    public static boolean darkMode = false;

    // 其他面板的參考，用於更新
    private CalendarPanel calendarPanel;
    private StatsPanel statsPanel;

    public TaskPanel() {
        loadSavedTasks();
        view = createView();
    }

    public void setCalendar(DatePicker calendar) {
        this.calendar = calendar;
    }

    public void setCalendarPanel(CalendarPanel calendarPanel) {
        this.calendarPanel = calendarPanel;
    }

    public void setStatsPanel(StatsPanel statsPanel) {
        this.statsPanel = statsPanel;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public VBox getView() {
        return view;
    }

    private TaskRepository taskRepo = new TaskRepository();

    private void handleExportTasks() {
        taskRepo.saveTasks(taskManager.getTasks());
        showAlert("任務已存檔到 tasks.json", Alert.AlertType.INFORMATION);
    }

    private void loadSavedTasks() {
        for (Task task : taskRepo.loadTasks()) {
            taskManager.addTask(task);
            taskListView.getItems().add(task);
        }
    }

    private VBox createView() {
        // 搜尋欄位設定
        searchField.setPromptText("搜尋任務...");
        searchField.setOnKeyReleased(e -> filterTasks());

        // 新任務輸入欄位與提示
        inputField.setPromptText("輸入任務...");

        // 下拉選單初始化（分類、重要性、週期）
        categoryChoice.getItems().addAll("工作", "個人", "其他");
        priorityChoice.getItems().addAll("低", "中", "高");
        recurrenceChoice.getItems().addAll("無", "每日", "每週", "每月");
        // 預設選項
        categoryChoice.getSelectionModel().select("其他");
        priorityChoice.getSelectionModel().select("中");
        recurrenceChoice.getSelectionModel().select("無");

        categoryChoice.setPromptText("分類");
        priorityChoice.setPromptText("重要性");
        recurrenceChoice.setPromptText("週期");

        categoryChoice.setPrefWidth(80);
        priorityChoice.setPrefWidth(80);
        recurrenceChoice.setPrefWidth(80);

        tagField.setPromptText("標籤（逗號分隔）");

        // 功能按鈕
        Button addButton = new Button("新增任務");
        Button completeButton = new Button("標記完成");
        Button deleteButton = new Button("刪除任務");
        Button exportButton = new Button("匯出任務");

        // 綁定按鈕事件處理
        addButton.setOnAction(e -> handleAddTask());
        completeButton.setOnAction(e -> handleCompleteTask());
        deleteButton.setOnAction(e -> handleDeleteTask());
        exportButton.setOnAction(e -> handleExportTasks());
        darkModeToggle.setOnAction(e -> {
            darkMode = darkModeToggle.isSelected();
            if (darkMode) {
                applyDarkTheme();
                if (calendarPanel != null) calendarPanel.applyTheme(true);
                if (statsPanel != null) statsPanel.applyTheme(true);
            } else {
                applyLightTheme();
                if (calendarPanel != null) calendarPanel.applyTheme(false);
                if (statsPanel != null) statsPanel.applyTheme(false);
            }
            taskListView.refresh(); // 刷新列表以套用新配色
        });

        // 設定 ListView 的 CellFactory 以支援任務卡片樣式和拖曳排序
        taskListView.setCellFactory(listView -> new ListCell<Task>() {
            {
                // 設定拖曳偵測事件
                setOnDragDetected(event -> {
                    if (getItem() == null) return;
                    Dragboard db = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(Integer.toString(getListView().getItems().indexOf(getItem())));
                    db.setContent(content);
                    event.consume();
                });
                // 拖曳目標進入此 Cell 範圍
                setOnDragOver(event -> {
                    if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });
                setOnDragEntered(event -> {
                    if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                        setOpacity(0.3);
                    }
                });
                setOnDragExited(event -> {
                    if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                        setOpacity(1);
                    }
                });
                // 放開拖曳，在此 Cell 進行釋放
                setOnDragDropped(event -> {
                    if (getItem() == null) return;
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        ObservableList<Task> items = getListView().getItems();
                        int draggedIndex = Integer.parseInt(db.getString());
                        int thisIndex = items.indexOf(getItem());
                        Task draggedTask = items.remove(draggedIndex);
                        if (draggedIndex < thisIndex) {
                            // 如果拖曳項目原先在目標之前，移除後目標索引需減1
                            thisIndex--;
                        }
                        items.add(thisIndex, draggedTask);
                        // 更新底層任務清單的順序
                        taskManager.getTasks().clear();
                        taskManager.getTasks().addAll(items);
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
                setOnDragDone(event -> event.consume());
            }
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setTooltip(null);
                    setStyle("");
                } else {
                    // 顯示任務文字（含完成勾選符號和日期）
                    setText(task.toString());
                    // 設定包含任務詳細資訊的提示（滑鼠懸停顯示分類、標籤、重要性等）
                    String details = "";
                    details += "分類: " + task.getCategory();
                    details += "\n標籤: " + (task.getTags().isEmpty() ? "（無）" : task.getTags());
                    details += "\n重要性: ";
                    details += (task.getPriority() == 3 ? "高" : (task.getPriority() == 2 ? "中" : "低"));
                    details += "\n截止日期: " + task.getDeadline();
                    if (!task.getRecurrence().equals("無")) {
                        details += "\n週期: " + task.getRecurrence();
                    }
                    setTooltip(new Tooltip(details));
                    // 根據主題切換設定文字與背景樣式
                    if (darkMode) {
                        setTextFill(Color.WHITE);
                        if (isSelected()) {
                            setStyle("-fx-background-color: #666666; -fx-border-color: #888888; -fx-border-radius: 4; -fx-padding: 4;");
                        } else {
                            setStyle("-fx-background-color: #444444; -fx-border-color: #777777; -fx-border-radius: 4; -fx-padding: 4;");
                        }
                    } else {
                        setTextFill(Color.BLACK);
                        // 淺色模式下僅設置邊框，背景使用預設（可顯示選取高亮）
                        setStyle("-fx-border-color: #cccccc; -fx-border-radius: 4; -fx-padding: 4;");
                    }
                }
            }
        });

        // 組合版面配置
        HBox inputBox = new HBox(10, inputField, addButton);
        HBox advancedOptionsBox1 = new HBox(10, categoryChoice, priorityChoice, recurrenceChoice);
        HBox actionButtons = new HBox(10, completeButton, deleteButton, exportButton, darkModeToggle);

        VBox panel = new VBox(10, searchField, taskListView, inputBox, advancedOptionsBox1, tagField, actionButtons);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(380);
        return panel;
    }

    private void handleAddTask() {
        String title = inputField.getText().trim();
        LocalDate selectedDate = (calendar != null) ? calendar.getValue() : null;
        if (!title.isEmpty() && selectedDate != null) {
            // 取得選擇的屬性，若未選則使用預設值
            String category = categoryChoice.getValue() != null ? categoryChoice.getValue() : "其他";
            String priorityText = priorityChoice.getValue() != null ? priorityChoice.getValue() : "中";
            String recurrence = recurrenceChoice.getValue() != null ? recurrenceChoice.getValue() : "無";
            String tagsInput = tagField.getText().trim();
            // 整理標籤字串（去除空白與多餘逗號）
            String tags = "";
            if (!tagsInput.isEmpty()) {
                String[] tagArr = tagsInput.split(",");
                List<String> tagList = new ArrayList<>();
                for (String t : tagArr) {
                    String tg = t.trim();
                    if (!tg.isEmpty()) tagList.add(tg);
                }
                tags = String.join(", ", tagList);
            }
            // 將重要性文字轉為數值
            int priority = 1;
            if (priorityText.equals("高")) priority = 3;
            else if (priorityText.equals("中")) priority = 2;
            // 建立新任務並加入清單
            Task task = new Task(title, selectedDate);
            task.setCategory(category);
            task.setPriority(priority);
            task.setTags(tags);
            task.setRecurrence(recurrence);
            taskManager.addTask(task);
            taskListView.getItems().add(task);
            inputField.clear();
            // 清空標籤欄位供下次輸入
            tagField.clear();
            // 更新統計圖表與行事曆顯示
            if (statsPanel != null) statsPanel.updateCharts();
            if (calendarPanel != null) {
                calendarPanel.refreshCalendarView();
            }
        } else {
            showAlert("請先輸入任務名稱並選擇日期！", Alert.AlertType.WARNING);
        }
    }

    private void handleCompleteTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.toggleCompleted();
            // 若標記完成後的任務為週期性任務，則自動新增下一次
            if (selected.isCompleted() && selected.isRecurring()) {
                LocalDate nextDate = selected.getDeadline();
                switch (selected.getRecurrence()) {
                    case "每日":
                        nextDate = nextDate.plusDays(1);
                        break;
                    case "每週":
                        nextDate = nextDate.plusWeeks(1);
                        break;
                    case "每月":
                        nextDate = nextDate.plusMonths(1);
                        break;
                }
                Task newTask = new Task(selected.getTitle(), nextDate);
                newTask.setCategory(selected.getCategory());
                newTask.setPriority(selected.getPriority());
                newTask.setTags(selected.getTags());
                newTask.setRecurrence(selected.getRecurrence());
                taskManager.addTask(newTask);
                taskListView.getItems().add(newTask);
                // 彈出資訊提示新任務已建立
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("週期任務提示");
                info.setHeaderText(null);
                info.setContentText("已新增下一次的週期性任務: " + selected.getTitle() + "，截止日期: " + nextDate);
                info.showAndWait();
            }
            taskListView.refresh();
            if (statsPanel != null) statsPanel.updateCharts();
            if (calendarPanel != null) {
                calendarPanel.refreshCalendarView();
            }
        } else {
            showAlert("請選擇要標記完成的任務！", Alert.AlertType.WARNING);
        }
    }

    private void handleDeleteTask() {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskManager.getTasks().remove(selected);
            taskListView.getItems().remove(selected);
            if (statsPanel != null) statsPanel.updateCharts();
            if (calendarPanel != null) {
                calendarPanel.refreshCalendarView();
            }
        } else {
            showAlert("請選擇要刪除的任務！", Alert.AlertType.WARNING);
        }
    }

    private void filterTasks() {
        String query = searchField.getText().trim().toLowerCase();
        taskListView.getItems().clear();
        if (query.isEmpty()) {
            // 顯示所有任務
            for (Task task : taskManager.getTasks()) {
                taskListView.getItems().add(task);
            }
        } else {
            for (Task task : taskManager.getTasks()) {
                String title = task.getTitle().toLowerCase();
                String category = task.getCategory().toLowerCase();
                String tags = task.getTags().toLowerCase();
                if (title.contains(query) || category.contains(query) || tags.contains(query)) {
                    taskListView.getItems().add(task);
                }
            }
        }
    }

    private void applyDarkTheme() {
        view.setStyle("-fx-background-color: #2b2b2b;");
        taskListView.setStyle("-fx-background-color: #333333;");
    }

    private void applyLightTheme() {
        view.setStyle("-fx-background-color: transparent;");
        taskListView.setStyle("");
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.WARNING ? "警告" : "通知");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // 檢查截止期限並提醒
    public void checkDeadlines() {
        LocalDate today = LocalDate.now();
        StringBuilder overdueList = new StringBuilder();
        for (Task task : taskManager.getTasks()) {
            if (!task.isCompleted()) {
                LocalDate due = task.getDeadline();
                if (due.isBefore(today) || due.equals(today)) {
                    String status = due.isBefore(today) ? "（已過期）" : "（今天截止）";
                    overdueList.append(task.toString()).append(status).append("\n");
                }
            }
        }
        if (overdueList.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("到期提醒");
            alert.setHeaderText("以下任務已到期或即將到期：");
            alert.setContentText(overdueList.toString());
            alert.showAndWait();
        }
    }
}
