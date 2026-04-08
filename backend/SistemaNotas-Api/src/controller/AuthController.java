package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import service.AuthService;
import util.HttpHelper;

import java.io.IOException;
import java.util.Map;

public class AuthController implements HttpHandler {

    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Manejar preflight CORS
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);

        try {
            if (path.equals("/api/auth/login") && method.equals("POST")) {
                handleLogin(exchange);
            } else if (path.equals("/api/auth/logout") && method.equals("POST")) {
                handleLogout(exchange);
            } else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor");
        }
    }

    // POST /api/auth/login
    // Body: { "correo": "admin@mail.com", "password": "1234" }
    // Response: { "token": "uuid", "id": 1, "nombre": "Admin", "correo": "...", "rol": "admin" }
    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);

        String correo = (String) body.get("correo");
        String password = (String) body.get("password");

        Map<String, Object> result = authService.login(correo, password);

        if (result != null) {
            HttpHelper.sendJson(exchange, 200, result);
        } else {
            HttpHelper.sendError(exchange, 401, "Correo o contraseña incorrectos");
        }
    }

    // POST /api/auth/logout
    // Header: Authorization: Bearer <token>
    // Response: { "mensaje": "Sesion cerrada" }
    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = HttpHelper.getToken(exchange);
        authService.logout(token);
        HttpHelper.sendJson(exchange, 200, Map.of("mensaje", "Sesion cerrada"));
    }
}