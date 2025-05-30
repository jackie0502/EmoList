package com.app.emolist.GUI;
import com.app.emolist.Controller.Task;
import com.app.emolist.Controller.TaskManager;
import com.app.emolist.DataBase.TaskRepository;
import com.app.emolist.GUI.TaskPanel.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

public class TaskPanelController {

    @FXML private HBox categoryTabs;
    @FXML private ComboBox<String> categoryDropdown;
    @FXML private HBox addCategoryBox;
    @FXML private TextField addCategoryField;
    @FXML private Label categoryMessage;
    @FXML private ComboBox<String> taskCategoryChoice;
    @FXML private ListView<Task> uncompletedListView;
    @FXML private ListView<Task> completedListView;
    @FXML private TextField searchField;
    @FXML private TextField inputField;
    @FXML private ComboBox<String> priorityChoice;
    @FXML private ComboBox<String> recurrenceChoice;
    @FXML private Button notificationButton;
    @FXML private HBox notificationBox;
    @FXML private Button darkModeButton;
    @FXML private Region categorySpacer;
    @FXML private VBox taskInputBox;
    @FXML
    public CheckBox enableNotificationCheckBox;
    @FXML
    public ComboBox<Integer> daysBeforeChoice;
    @FXML
    public Spinner<Integer> hourSpinner;
    @FXML
    public Spinner<Integer> minuteSpinner;
    @FXML private Label timeColon;


    private final TaskManager taskManager = new TaskManager();
    private final TaskRepository taskRepo = new TaskRepository();

    private String currentCategoryFilter = "全部";
    private final Set<Task> selectedTasks = new HashSet<>();


    // 子模組
    private final CategoryHelper categoryHelper = new CategoryHelper(this);
    private final TaskInputHelper taskInputHelper = new TaskInputHelper(this);
    private final TaskViewHelper taskViewHelper = new TaskViewHelper(this);
    private final DeadlineHelper deadlineHelper = new DeadlineHelper(this);
    public Set<Task> getSelectedTasks() { return selectedTasks; }
    private CalendarPanelController calendarController;
    private StatsPanelController statsController;
    private boolean isDarkMode = false;


    @FXML
    private void initialize() {
        taskCategoryChoice.getItems().addAll("工作", "娛樂", "無");
        taskCategoryChoice.getSelectionModel().select("無");
        priorityChoice.getItems().addAll("低", "中", "高");
        priorityChoice.getSelectionModel().select("中");

        recurrenceChoice.getItems().addAll("無", "每日", "每週", "每月");
        recurrenceChoice.getSelectionModel().select("無");

        categoryHelper.refreshCategoryTabs();
        taskInputHelper.hideTaskInputBox();
        taskViewHelper.setupListViews();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

    }

    // 👇 提供給子模組呼叫的橋接方法們

    public void showAddCategoryInput() {
        addCategoryBox.setVisible(true);
        addCategoryBox.setManaged(true);
        addCategoryField.clear();
        categoryMessage.setText("");
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager.getAllTasks().clear();
        this.taskManager.getAllTasks().addAll(taskManager.getAllTasks());
    }

    @FXML
    private void handleCompleteSelectedTasks() {
        // ✅ 用 ArrayList 篩選出尚未完成的任務
        ArrayList<Task> incompleteTasks = new ArrayList<>();
        for (Task task : selectedTasks) {
            if (!task.isCompleted()) {
                incompleteTasks.add(task);
            }
        }

        // ✅ 若都是已完成任務，直接跳出不處理
        if (incompleteTasks.isEmpty()) return;

        // ✅ 顯示壓力指數對話框（只針對未完成任務）
        PressureDialog dialog = new PressureDialog(new ArrayList<>(incompleteTasks));
        Map<Task, Integer> stressMap = dialog.showAndWait();
        if (stressMap == null) return;

        for (Task task : incompleteTasks) {
            task.setCompleted(true);

            // ✅ 記錄壓力指數
            if (stressMap.containsKey(task)) {
                task.setStressLevel(stressMap.get(task));
            }

            // 🔁 若有 recurrence，產生新任務
            if (!"無".equals(task.getRecurrence()) && task.getDeadline() != null) {
                LocalDate nextDeadline = null;
                switch (task.getRecurrence()) {
                    case "每天":
                        nextDeadline = task.getDeadline().plusDays(1);
                        break;
                    case "每週":
                        nextDeadline = task.getDeadline().plusWeeks(1);
                        break;
                    case "每月":
                        nextDeadline = task.getDeadline().plusMonths(1);
                        break;
                }

                if (nextDeadline != null) {
                    Task newTask = new Task(
                            task.getTitle(),
                            nextDeadline,
                            task.getCategory(),
                            task.getPriority(),
                            task.getTags(),
                            task.getRecurrence()
                    );
                    taskManager.addTask(newTask);
                }
            }
        }

        updatePanels(); // ✅ 更新日曆與統計圖表
        selectedTasks.clear();
        refreshTaskViews();
        taskRepo.saveTasks(taskManager.getAllTasks());
    }

    @FXML
    private void handleUncompleteTask() {
        if (selectedTasks.isEmpty()) return;

        for (Task task : selectedTasks) {
            task.setCompleted(false);
            task.setStressLevel(0);
        }

        selectedTasks.clear();
        refreshTaskViews();
        updatePanels();
    }


    public void setCalendarController(CalendarPanelController calendarController) {
        this.calendarController = calendarController;
        calendarController.setTaskManager(this.taskManager); // 👈 加上這行
    }

    public void setStatsController(StatsPanelController statsController) {
        this.statsController = statsController;
        this.statsController.setTaskManager(this.taskManager); // 加上這行，讓 StatsPanel 有資料
    }


    @FXML
    private void handleShowAddCategory() {
        showAddCategoryInput(); // 或直接在這裡顯示分類輸入欄位
    }



    @FXML
    private void handleConfirmAddCategory() {
        String newCategory = addCategoryField.getText().trim();
        if (newCategory.isEmpty()) {
            categoryMessage.setText("分類不能為空");
        } else if (getVisualLength(newCategory) > 10) {
            categoryMessage.setText("分類長度不能超過 10（中文算2）");
        } else if (taskCategoryChoice.getItems().contains(newCategory)) {
            categoryMessage.setText("分類已存在");
        } else {
            taskCategoryChoice.getItems().add(newCategory);
            categoryHelper.refreshCategoryTabs();
            categoryMessage.setText("已新增分類：" + newCategory);
            addCategoryBox.setVisible(false);
            addCategoryBox.setManaged(false);
        }
    }

    @FXML
    private void handleCancelAddCategory() {
        addCategoryBox.setVisible(false);
        addCategoryBox.setManaged(false);
        categoryMessage.setText("");
    }

    @FXML
    private void handleShowTaskInput() {
        taskInputHelper.showTaskInputBox();
    }

    @FXML
    private void handleAddTask() {
        taskInputHelper.handleAddTask();
        if (enableNotificationCheckBox.isSelected()) {
            Integer daysBefore = daysBeforeChoice.getValue();
            Integer hour = hourSpinner.getValue();
            Integer minute = minuteSpinner.getValue();

            if (daysBefore != null && hour != null && minute != null) {
                System.out.println("✅ 通知設定：提前 " + daysBefore + " 天，" + hour + ":" + String.format("%02d", minute));
                // 可存在 Task 中、或傳給通知模組儲存
            }
        }

        if (statsController != null) {
            statsController.updateCharts();
        }
        refreshTaskViews();
        updatePanels(); // 👈 這裡會更新日曆
        if (calendarController != null) {
            calendarController.refreshCalendarView(); // 👈 重複呼叫
        }
        if (statsController != null) {
            statsController.updateCharts();
        }

        taskRepo.saveTasks(taskManager.getAllTasks());
    }

    @FXML
    private void handleCancelAddTask() {
        taskInputHelper.hideTaskInputBox();
    }

    @FXML
    private void handleDeleteTask() {
        if (selectedTasks.isEmpty()) return;

        taskManager.getAllTasks().removeIf(selectedTasks::contains);

        selectedTasks.clear();
        refreshTaskViews();
        updatePanels(); // 👈 這裡會更新日曆
        if (calendarController != null) {
            calendarController.refreshCalendarView(); // 👈 重複呼叫
        }
        if (statsController != null) {
            statsController.updateCharts();
        }

        taskRepo.saveTasks(taskManager.getAllTasks());
    }

    @FXML
    private void handleToggleNotificationButton() {
        notificationBox.setVisible(true);
        notificationBox.setManaged(true);
    }

    @FXML void handleAddNotification(){
        notificationBox.setVisible(false);
        notificationBox.setManaged(false);
    }

    @FXML void handleCancelAddNotification(){
        notificationBox.setVisible(false);
        notificationBox.setManaged(false);
    }

    @FXML
    private void handleExportTasks() {
        taskRepo.saveTasks(taskManager.getAllTasks());
    }

    @FXML
    private void toggleDarkMode() {
        Scene scene = darkModeButton.getScene();
        if (scene == null) return;

        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            uncompletedListView.getScene().getRoot().getStyleClass().add("dark-mode");
            darkModeButton.setText("淺色模式");
        } else {
            uncompletedListView.getScene().getRoot().getStyleClass().remove("dark-mode");
            darkModeButton.setText("深色模式");
        }
    }


    public void refreshTaskViews() {
        taskViewHelper.refreshTaskViews();
    }

//    public LocalDate getDeadLine(){
//        deadlineHelper
//    }
    public void checkDeadlines() {
        deadlineHelper.checkDeadlines();
    }

    public void updatePanels() {
        // 若有 stats/calendar 可由這裡串接更新
        if (statsController != null) {
            statsController.updateCharts();
        }
        if (calendarController != null) {
            calendarController.refreshCalendarView();  // <-- 這行必須有
        }
    }

    // 🔧 視覺長度工具
    private int getVisualLength(String s) {
        int length = 0;
        for (char c : s.toCharArray()) {
            if (Character.UnicodeBlock.of(c).toString().contains("CJK") ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }

    @FXML private DatePicker deadlinePicker;

    public DatePicker getTaskDeadlinePicker() {
        return deadlinePicker;
    }

    @FXML
    private void handleClearDeadline() {
        deadlinePicker.setValue(null);
    }

    public CalendarPanelController getCalendarController() {
        return calendarController;
    }

    // 👉 getter
    public TaskManager getTaskManager() { return taskManager; }
    public TaskRepository getTaskRepo() { return taskRepo; }

    public HBox getCategoryTabs() { return categoryTabs; }
    public ComboBox<String> getCategoryDropdown() { return categoryDropdown; }
    public HBox getAddCategoryBox() { return addCategoryBox; }
    public TextField getAddCategoryField() { return addCategoryField; }
    public Label getCategoryMessage() { return categoryMessage; }

    public TextField getSearchField() { return searchField; }
    public TextField getInputField() { return inputField; }
    public ComboBox<String> getTaskCategoryChoice() { return taskCategoryChoice; }
    public ComboBox<String> getPriorityChoice() { return priorityChoice; }
    public ComboBox<String> getRecurrenceChoice() { return recurrenceChoice; }

    public ListView<Task> getUncompletedListView() { return uncompletedListView; }
    public ListView<Task> getCompletedListView() { return completedListView; }

    public VBox getTaskInputBox() { return taskInputBox; }

    public String getCurrentCategoryFilter() { return currentCategoryFilter; }
    public void setCurrentCategoryFilter(String filter) { this.currentCategoryFilter = filter; }

    @FXML
    private void handleToggleNotificationOptions() {
        boolean enabled = enableNotificationCheckBox.isSelected();

        daysBeforeChoice.setVisible(enabled);
        daysBeforeChoice.setManaged(enabled);

        hourSpinner.setVisible(enabled);
        hourSpinner.setManaged(enabled);

        minuteSpinner.setVisible(enabled);
        minuteSpinner.setManaged(enabled);

        timeColon.setVisible(enabled);
        timeColon.setManaged(enabled);
    }


    public void handleToggleEnableNotification(ActionEvent actionEvent) {
    }
}
