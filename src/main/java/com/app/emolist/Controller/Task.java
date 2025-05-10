package com.app.emolist.Controller;


import java.time.LocalDate;

public class Task {
    private String title;
    private boolean isCompleted;
    private LocalDate deadline;

    public Task(String title, LocalDate deadline) {
        this.title = title;
        this.deadline = deadline;
        this.isCompleted = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void toggleCompleted() {
        isCompleted = !isCompleted;
    }

    @Override
    public String toString() {
        return (isCompleted ? "[✔] " : "[ ] ") + title +
                (deadline != null ? " (" + deadline.toString() + ")" : "");
    }
}
