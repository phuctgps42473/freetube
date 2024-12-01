package utils;

public class XJsonErrorBody {
    public static String message(String message) {
        return "{\"message\":\"" + message + "\"}";
    }

    public static String badRequest() {
        return "{\"message\":\"Bad request\"}";
    }

    public static String notAllowed() {
        return "{\"message\":\"You are not allowed to perform this action\"}";
    }
}
