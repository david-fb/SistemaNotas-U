package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Semestre;
import model.Usuario;
import service.SemestreService;
import util.HttpHelper;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SemestreController implements HttpHandler {

    private final SemestreService semestreService = new SemestreService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Manejo de CORS obligatorio
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);

        try {
            // Validación obligatoria de autenticación
            Usuario solicitante = HttpHelper.getUsuario(exchange);
            if (solicitante == null) {
                HttpHelper.sendError(exchange, 401, "No autorizado. Inicia sesión primero.");
                return;
            }

            // GET /api/semestres → lista todos (todos los roles)
            if (path.equals("/api/semestres") && method.equals("GET")) {
                handleGetAll(exchange);
            }
            // POST /api/semestres → crea semestre (solo admin)
            else if (path.equals("/api/semestres") && method.equals("POST")) {
                if (!solicitante.getRol().equals("admin")) {
                    HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol admin.");
                    return;
                }
                handleCreate(exchange);
            }
            // Ruta no encontrada
            else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada en semestres");
            }

        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            HttpHelper.sendError(exchange, 500, "Error interno del servidor en semestres");
        }
    }

    // GET /api/semestres → devuelve la lista de semestres
    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Semestre> semestres = semestreService.getAll();
        List<Map<String, Object>> lista = semestres.stream()
                .map(Semestre::toMap)
                .collect(Collectors.toList());
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }

    // POST /api/semestres → crea un nuevo semestre
    private void handleCreate(HttpExchange exchange) throws IOException {
        try {
            Map<String, Object> body = HttpHelper.readJsonBody(exchange);

            if (body == null || body.isEmpty()) {
                HttpHelper.sendError(exchange, 400, "El cuerpo JSON está vacío");
                return;
            }

            String nombre = (String) body.get("nombre");
            String fechaInicio = (String) body.get("fechaInicio");
            String fechaFin = (String) body.get("fechaFin");

            // Crear el semestre
            Semestre creado = semestreService.create(nombre, fechaInicio, fechaFin);

            if (creado != null) {
                HttpHelper.sendJson(exchange, 201, creado.toMap());
            } else {
                HttpHelper.sendError(exchange, 500, "No se pudo recuperar el semestre creado");
            }

        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            HttpHelper.sendError(exchange, 500, "Error interno del servidor en semestres");
        } finally {
            exchange.close();
        }
    }
}
