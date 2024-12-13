package utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class XJsonMapper {
    /**
     * Dynamically maps the fields of an object, including nested objects, based on requested field names.
     *
     * @param object The source object to map from.
     * @param fields The requested field names, including nested fields (e.g., "profile.bio").
     * @return A Map containing the requested field names and their corresponding values.
     */
    public static Map<String, Object> mapFields(Object object, String... fields) {
        Map<String, Object> mappedData = new HashMap<>();

        if (object == null || fields == null) {
            return mappedData; // Return an empty map for null inputs
        }

        for (String field : fields) {
            try {
                if (field.contains(".")) {
                    // Handle nested fields (e.g., "profile.bio")
                    String[] parts = field.split("\\.", 2); // Split into parent and child fields
                    String parentField = parts[0];
                    String childField = parts[1];

                    // Get the parent object using reflection
                    String methodName = "get" + parentField.substring(0, 1).toUpperCase() + parentField.substring(1);
                    Method method = object.getClass().getMethod(methodName);
                    Object parentObject = method.invoke(object);

                    if (parentObject != null) {
                        // Recursively map the child fields
                        Map<String, Object> nestedMap = mapFields(parentObject, childField);
                        mappedData.put(parentField, nestedMap);
                    }
                } else {
                    // Handle simple fields
                    String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
                    Method method = object.getClass().getMethod(methodName);
                    Object value = method.invoke(object);
                    mappedData.put(field, value);
                }
            } catch (Exception e) {
                // Handle cases where the field doesn't exist or cannot be accessed
                System.err.println("Field " + field + " not found or inaccessible: " + e.getMessage());
            }
        }

        return mappedData;
    }
}
