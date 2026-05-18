package util;

import com.sun.net.httpserver.HttpExchange;
import model.Usuario;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpHelper {

    // Lee el body de la peticion y lo devuelve como String
    public static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        byte[] bytes = is.readAllBytes();
        is.close();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // Lee el body y lo parsea como JSON a Map
    public static Map<String, Object> readJsonBody(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        return JsonUtil.parse(body);
    }

    // Envia una respuesta JSON con codigo de estado
    public static void sendJson(HttpExchange exchange, int statusCode, Map<String, Object> data) throws IOException {
        String json = JsonUtil.toJson(data);
        sendRawJson(exchange, statusCode, json);
    }

    // Envia un JSON array como respuesta
    public static void sendJsonArray(HttpExchange exchange, int statusCode, String jsonArray) throws IOException {
        sendRawJson(exchange, statusCode, jsonArray);
    }

    // Envia un string JSON raw como respuesta
    public static void sendRawJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // Envia una respuesta de error con mensaje
    public static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> error = Map.of("error", message);
        sendJson(exchange, statusCode, error);
    }

    // Maneja las peticiones OPTIONS (preflight de CORS)
    public static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    // Extrae el token del header Authorization
    // Formato esperado: "Bearer <token>"
    public static String getToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    // Retorna el Usuario autenticado — disponible tras pasar por AuthMiddleware
    public static Usuario getUsuario(HttpExchange exchange) {
        return (Usuario) exchange.getAttribute("usuario");
    }

    // Extrae el metodo HTTP (GET, POST, PUT, DELETE)
    public static String getMethod(HttpExchange exchange) {
        return exchange.getRequestMethod().toUpperCase();
    }

    // Extrae un segmento de la ruta por posicion
    // Ejemplo: /api/usuarios/5 -> getPathParam(exchange, 3) retorna "5"
    public static String getPathParam(HttpExchange exchange, int index) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (index < parts.length) {
            return parts[index];
        }
        return null;
    }

    // Extrae un query param de la URL
    // Ejemplo: /api/usuarios?rol=estudiante -> getQueryParam(exchange, "rol") retorna "estudiante"
    public static String getQueryParam(HttpExchange exchange, String name) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair[0].equals(name) && pair.length > 1) {
                return pair[1];
            }
        }
        return null;
    }
}
