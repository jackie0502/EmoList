package com.app.emolist.DataBase;

import com.app.emolist.Controller.Task;
import java.util.List;

public class TaskRepository {
    public void saveTasks(List<Task> tasks) {
        // 實作儲存邏輯（檔案、資料庫等）
    }

    public List<Task> loadTasks() {
        // 實作載入邏輯
        return List.of(); // 暫時回傳空清單
    }
}
