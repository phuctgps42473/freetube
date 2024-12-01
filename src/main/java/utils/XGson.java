package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class XGson {
    public static Gson createGson(boolean onlyExpose) {
        if (onlyExpose) {
            return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        } else {
            return new GsonBuilder().create();
        }
    }
}
