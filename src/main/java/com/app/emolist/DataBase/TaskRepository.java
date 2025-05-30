package com.app.emolist.DataBase;

import com.app.emolist.Controller.Task;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TaskRepository {
    private static final String DEFAULT_PATH = "data/tasks.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();


    // LocalDate 序列化/反序列化 Adapter
    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        @Override
        public JsonElement serialize(LocalDate date, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(formatter));
        }
        @Override
        public LocalDate deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }

    // 儲存任務
    public static boolean saveTasks(List<Task> tasks) {
        File file = new File(DEFAULT_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (Writer writer = new FileWriter(file)) {  // 直接用正式檔案路徑寫
            gson.toJson(tasks, writer);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    // 載入任務
    public static List<Task> loadTasks() {
        File file = new File(DEFAULT_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Task>>() {}.getType();
            List<Task> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            // 異常處理：資料損壞時回傳空清單
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

