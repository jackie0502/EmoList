package com.app.emolist.Controller;

import java.time.LocalDate;

public class Task {
    private String title;
    private boolean isCompleted;
    private LocalDate deadline;
    private String category;
    private int priority;
    private String tags;
    private String recurrence;

    public Task(String title, LocalDate deadline) {
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = false;
        // Default values for new fields
        this.category = "其他";      // 預設分類「其他」
        this.priority = 1;          // 預設重要性等級1（低）
        this.tags = "";             // 預設無標籤
        this.recurrence = "無";     // 預設無週期
    }

    // 可選的建構子，一次傳入所有欄位
    public Task(String title, LocalDate deadline, String category, int priority, String tags, String recurrence) {
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = false;
        this.category = (category != null ? category : "其他");
        this.priority = priority;
        this.tags = (tags != null ? tags : "");
        this.recurrence = (recurrence != null ? recurrence : "無");
    }

    public String getTitle() {return title;}
    public boolean isCompleted() {return isCompleted;}
    public LocalDate getDeadline() {return deadline;}
    public String getCategory() {return category;}
    public int getPriority() {return priority;}
    public String getTags() {return tags;}
    public String getRecurrence() {return recurrence;}

    public void setCompleted(boolean completed) {this.isCompleted = completed;}
    public void setCategory(String category) {this.category = category;}
    public void setPriority(int priority) {this.priority = priority;}
    public void setTags(String tags) {this.tags = tags;}
    public void setRecurrence(String recurrence) {this.recurrence = recurrence;}

    public boolean isRecurring() {return recurrence != null && !recurrence.equals("無");}
    public void toggleCompleted() {isCompleted = !isCompleted;}

}
