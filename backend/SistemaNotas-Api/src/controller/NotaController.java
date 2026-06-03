/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Nota;
import model.Usuario;
import service.NotaService;
import util.HttpHelper;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotaController implements HttpHandler {

    private final NotaService notaService = new NotaService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);
        String idStr = HttpHelper.getPathParam(exchange, 3); // /api/notas/{id}

        try {
            // GET /api/notas?cursoId=1 → notas de todos los estudiantes del curso
            if (path.matches("/api/notas/curso/\\d+") && method.equals("GET")) {
                handleGetByCurso(exchange, Integer.parseInt(HttpHelper.getPathParam(exchange, 4)));

            // POST /api/notas → registrar nota (solo profesor)
            } else if (path.equals("/api/notas") && method.equals("POST")) {
                handleCreate(exchange);

            // PUT /api/notas/{id} → editar nota (solo profesor)
            } else if (path.matches("/api/notas/\\d+") && method.equals("PUT")) {
                handleUpdate(exchange, Integer.parseInt(idStr));

            // DELETE /api/notas/{id} → eliminar nota (solo profesor)
            } else if (path.matches("/api/notas/\\d+") && method.equals("DELETE")) {
                handleDelete(exchange, Integer.parseInt(idStr));

            } else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada");
            }

        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor");
        }
    }

    // GET /api/notas?cursoId=1
    // Response: [{id, estudianteId, corteId, valor}, ...]
    private void handleGetByCurso(HttpExchange exchange, int cursoId) throws IOException {
        List<Nota> notas = notaService.getByCurso(cursoId);
        List<Map<String, Object>> lista = notas.stream()
                .map(Nota::toMap)
                .collect(Collectors.toList());
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }

    // POST /api/notas
    // Body: { "estudianteId": 4, "corteId": 1, "valor": 4.5 }
    // Response: {id, estudianteId, corteId, valor}
    private void handleCreate(HttpExchange exchange) throws IOException {
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (!solicitante.getRol().equals("profesor")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol profesor.");
            return;
        }

        Map<String, Object> body = HttpHelper.readJsonBody(exchange);

        if (body.get("estudianteId") == null || body.get("corteId") == null || body.get("valor") == null) {
            HttpHelper.sendError(exchange, 400, "Se requieren los campos estudianteId, corteId y valor");
            return;
        }

        int estudianteId = ((Number) body.get("estudianteId")).intValue();
        int corteId      = ((Number) body.get("corteId")).intValue();
        double valor     = ((Number) body.get("valor")).doubleValue();

        Nota creada = notaService.create(estudianteId, corteId, valor);
        HttpHelper.sendJson(exchange, 201, creada.toMap());
    }

    // PUT /api/notas/{id}
    // Body: { "valor": 3.8 }
    // Response: {id, estudianteId, corteId, valor}
    private void handleUpdate(HttpExchange exchange, int id) throws IOException {
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (!solicitante.getRol().equals("profesor")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol profesor.");
            return;
        }

        Map<String, Object> body = HttpHelper.readJsonBody(exchange);

        if (body.get("valor") == null) {
            HttpHelper.sendError(exchange, 400, "Se requiere el campo valor");
            return;
        }

        double valor = ((Number) body.get("valor")).doubleValue();
        Nota actualizada = notaService.update(id, valor);
        HttpHelper.sendJson(exchange, 200, actualizada.toMap());
    }

    // DELETE /api/notas/{id}
    // Response: { "mensaje": "Nota eliminada" }
    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (!solicitante.getRol().equals("profesor")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol profesor.");
            return;
        }

        notaService.delete(id);
        HttpHelper.sendJson(exchange, 200, Map.of("mensaje", "Nota eliminada"));
    }
}
