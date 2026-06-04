package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Curso;
import service.CursoService;
import util.HttpHelper;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CursoController implements HttpHandler {

   private final CursoService cursoService = new CursoService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Manejo de CORS obligatorio para que conecte con tu Frontend
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);

        try {
            // Valida estrictamente que sea la ruta /api/cursos y el método GET
            if (path.equals("/api/cursos") && method.equals("GET")) {
                handleGetAll(exchange);
            } else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada en cursos");
            }
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor en cursos");
        }
    }

    // GET /api/cursos
    // Response: [{id, nombre, descripcion, ...}, ...]
    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Curso> cursos = cursoService.getAll(); // Obtiene la lista desde tu servicio
        
        // Convierte la lista de objetos Curso a una lista de Maps para el JSON
        List<Map<String, Object>> lista = cursos.stream()
                .map(Curso::toMap) // Requiere que tu clase Curso tenga el método toMap()
                .collect(Collectors.toList());
                
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }
}
