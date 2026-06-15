package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Usuario;
import service.UsuarioService;
import util.HttpHelper;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioController implements HttpHandler {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);
        String idStr = HttpHelper.getPathParam(exchange, 3); // /api/usuarios/{id}

        Usuario solicitante = HttpHelper.getUsuario(exchange);
        boolean esAdmin = solicitante.getRol().equals("admin");
        boolean esProfesor = solicitante.getRol().equals("profesor");
        boolean esGetPorId = path.matches("/api/usuarios/\\d+") && method.equals("GET");
        if (!esAdmin && !(esProfesor && esGetPorId)) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado. No tienes permisos para esta acción.");
            return;
        }

        try {
            if (path.equals("/api/usuarios") && method.equals("GET")) {
                handleGetAll(exchange);

            } else if (path.matches("/api/usuarios/\\d+") && method.equals("GET")) {
                handleGetById(exchange, Integer.parseInt(idStr));

            } else if (path.equals("/api/usuarios") && method.equals("POST")) {
                handleCreate(exchange);

            } else if (path.matches("/api/usuarios/\\d+") && method.equals("PUT")) {
                handleUpdate(exchange, Integer.parseInt(idStr));

            } else if (path.matches("/api/usuarios/\\d+") && method.equals("DELETE")) {
                handleDeactivate(exchange, Integer.parseInt(idStr));

            } else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada");
            }
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor");
        }
    }

    // GET /api/usuarios
    // Response: [{id, nombre, correo, rol, activo}, ...]
    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Usuario> usuarios = usuarioService.getAll();
        List<Map<String, Object>> lista = usuarios.stream()
                .map(Usuario::toMap)
                .collect(Collectors.toList());
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }

    // GET /api/usuarios/{id}
    // Response: {id, nombre, correo, rol, activo}
    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        Usuario usuario = usuarioService.getById(id);
        if (usuario == null) {
            HttpHelper.sendError(exchange, 404, "Usuario no encontrado");
            return;
        }
        HttpHelper.sendJson(exchange, 200, usuario.toMap());
    }

    // POST /api/usuarios
    // Body: { "nombre": "...", "correo": "...", "password": "...", "rol": "..." }
    // Response: {id, nombre, correo, rol, activo}
    private void handleCreate(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        Usuario creado = usuarioService.create(
                (String) body.get("nombre"),
                (String) body.get("correo"),
                (String) body.get("password"),
                (String) body.get("rol")
        );
        HttpHelper.sendJson(exchange, 201, creado.toMap());
    }

    // PUT /api/usuarios/{id}
    // Body: { "nombre": "...", "correo": "...", "rol": "..." }
    // Response: {id, nombre, correo, rol, activo}
    private void handleUpdate(HttpExchange exchange, int id) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        Usuario actualizado = usuarioService.update(
                id,
                (String) body.get("nombre"),
                (String) body.get("correo"),
                (String) body.get("rol")
        );
        HttpHelper.sendJson(exchange, 200, actualizado.toMap());
    }

    // DELETE /api/usuarios/{id}
    // Response: { "mensaje": "Usuario desactivado" }
    private void handleDeactivate(HttpExchange exchange, int id) throws IOException {
        usuarioService.deactivate(id);
        HttpHelper.sendJson(exchange, 200, Map.of("mensaje", "Usuario desactivado"));
    }
}
