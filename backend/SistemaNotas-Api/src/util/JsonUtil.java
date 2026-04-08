package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    // Convierte un Map a JSON string
    // Ejemplo: {"nombre": "Juan", "rol": "admin", "activo": true}
    public static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            sb.append(valueToJson(entry.getValue()));
            i++;
        }

        sb.append("}");
        return sb.toString();
    }

    // Convierte una lista de Maps a JSON array
    // Ejemplo: [{"id": 1, "nombre": "Juan"}, {"id": 2, "nombre": "Maria"}]
    public static String toJsonArray(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(list.get(i)));
        }

        sb.append("]");
        return sb.toString();
    }

    // Parsea un JSON string simple a Map
    // Soporta: strings, numeros, booleanos, null
    public static Map<String, Object> parse(String json) {
        Map<String, Object> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            // Saltar espacios y comas
            while (i < json.length() && (json.charAt(i) == ' ' || json.charAt(i) == ',' || json.charAt(i) == '\n' || json.charAt(i) == '\r')) i++;
            if (i >= json.length()) break;

            // Leer key
            if (json.charAt(i) != '"') break;
            i++;
            int keyStart = i;
            while (i < json.length() && json.charAt(i) != '"') i++;
            String key = json.substring(keyStart, i);
            i++; // cerrar comilla

            // Saltar : y espacios
            while (i < json.length() && (json.charAt(i) == ':' || json.charAt(i) == ' ')) i++;

            // Leer value
            if (i >= json.length()) break;

            if (json.charAt(i) == '"') {
                // String value
                i++;
                StringBuilder val = new StringBuilder();
                while (i < json.length() && json.charAt(i) != '"') {
                    if (json.charAt(i) == '\\' && i + 1 < json.length()) {
                        i++;
                        if (json.charAt(i) == '"') val.append('"');
                        else if (json.charAt(i) == '\\') val.append('\\');
                        else if (json.charAt(i) == 'n') val.append('\n');
                        else val.append(json.charAt(i));
                    } else {
                        val.append(json.charAt(i));
                    }
                    i++;
                }
                map.put(key, val.toString());
                i++; // cerrar comilla
            } else if (json.charAt(i) == 't' || json.charAt(i) == 'f') {
                // Boolean value
                if (json.substring(i).startsWith("true")) {
                    map.put(key, true);
                    i += 4;
                } else {
                    map.put(key, false);
                    i += 5;
                }
            } else if (json.charAt(i) == 'n') {
                // Null value
                map.put(key, null);
                i += 4;
            } else {
                // Number value
                int numStart = i;
                while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}' && json.charAt(i) != ' ' && json.charAt(i) != '\n') i++;
                String numStr = json.substring(numStart, i).trim();
                if (numStr.contains(".")) {
                    map.put(key, Double.parseDouble(numStr));
                } else {
                    map.put(key, Integer.parseInt(numStr));
                }
            }
        }

        return map;
    }

    // Convierte un valor Java a su representacion JSON
    private static String valueToJson(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escapeJson((String) value) + "\"";
        if (value instanceof Boolean) return value.toString();
        if (value instanceof Number) return value.toString();
        return "\"" + escapeJson(value.toString()) + "\"";
    }

    // Escapa caracteres especiales en strings JSON
    private static String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
