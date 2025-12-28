package ru.nugget.Ytiliti;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Messages {
    private static final String CONFIG_FILE = "Messages.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject configData;

    public static void loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            configData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            configData = new JsonObject();
        }
    }

    public static String getString(String path) {
        String[] keys = path.split("\\.");
        JsonElement current = configData;

        for (String key : keys) {
            if (current != null && current.isJsonObject()) {
                current = current.getAsJsonObject().get(key);
            }
        }
        return (current != null && current.isJsonPrimitive()) ? current.getAsString() : null;
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(configData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getJsonObject(String path) {
        String[] keys = path.split("\\.");
        JsonElement current = configData;

        for (String key : keys) {
            if (current != null && current.isJsonObject()) {
                current = current.getAsJsonObject().get(key);
            }
        }
        return (current != null && current.isJsonObject()) ? current.getAsJsonObject() : null;
    }
}