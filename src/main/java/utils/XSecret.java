package utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class XSecret {
    // Use volatile for thread-safety in case multiple threads are accessing the secrets map.
    private static volatile HashMap<String, String> secrets;

    public static String getSecret(String key) {
        // Double-checked locking pattern to ensure thread safety and lazy loading.
        if (secrets == null) {
            synchronized (XSecret.class) {
                if (secrets == null) {
                    String projectRoot = System.getenv("PROJECT_ROOT");
                    if (projectRoot == null) {
                        throw new RuntimeException("PROJECT_ROOT environment variable is not set.");
                    }

                    try (JsonReader jr = new JsonReader(new FileReader(projectRoot + System.getProperty("file.separator") + "src" + System.getProperty("file.separator") + "main" + System.getProperty("file.separator") + "java" + System.getProperty("file.separator") + "secret.json"))) {
                        secrets = new Gson().fromJson(jr, HashMap.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Error loading secrets from file", e);
                    }
                }
            }
        }

        String value = secrets.get(key);
        if (value == null) {
            throw new RuntimeException("Secret not found for key: " + key);
        }
        return value;
    }
}
