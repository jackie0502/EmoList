package com.app.emolist.Controller;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private final String id;
    private String title;
    private boolean isCompleted;
    private LocalDate deadline;
    private String category;
    private int priority;
    private String tags;
    private String recurrence;
    private int stressLevel = 0;

    public Task(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.deadline = LocalDate.MAX;
        this.isCompleted = false;
        // Default values for new fields
        this.category = "其他";      // 預設分類「其他」
        this.priority = 0;          // 預設Emo等級0
        this.tags = "";             // 預設無標籤
        this.recurrence = "無";     // 預設無週期
    }


    public Task(String title, LocalDate deadline) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = false;
        // Default values for new fields
        this.category = "其他";      // 預設分類「其他」
        this.priority = 0;          // 預設重要性等級1（低）
        this.tags = "";             // 預設無標籤
        this.recurrence = "無";     // 預設無週期
    }

    // 可選的建構子，一次傳入所有欄位
    public Task(String title, LocalDate deadline, String category, int priority, String tags, String recurrence) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = false;
        this.category = (category != null ? category : "其他");
        this.priority = priority;
        this.tags = (tags != null ? tags : "");
        this.recurrence = (recurrence != null ? recurrence : "無");
        this.stressLevel = 0;
    }


    public String getId() {return id;}
    public String getTitle() {return title;}
    public boolean isCompleted() {return isCompleted;}
    public LocalDate getDeadline() {return deadline;}
    public String getCategory() {return category;}
    public int getPriority() {return priority;}
    public String getTags() {return tags;}
    public String getRecurrence() {return recurrence;}
    public int getStressLevel() { return stressLevel; }

    public void setCompleted(boolean completed) {this.isCompleted = completed;}
    public void setCategory(String category) {this.category = category;}
    public void setPriority(int priority) {this.priority = priority;}
    public void setTags(String tags) {this.tags = tags;}
    public void setRecurrence(String recurrence) {this.recurrence = recurrence;}
    public void setStressLevel(int level) { this.stressLevel = level; }

    public boolean isRecurring() {return recurrence != null && !recurrence.equals("無");}
    public void toggleCompleted() {isCompleted = !isCompleted;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
