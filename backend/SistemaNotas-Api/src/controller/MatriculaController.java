package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Matricula;
import model.Usuario;
import service.MatriculaService;
import util.HttpHelper;
import util.JsonUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatriculaController implements HttpHandler {

    private final MatriculaService matriculaService = new MatriculaService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        // Validación obligatoria de autenticación
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (solicitante == null) {
            HttpHelper.sendError(exchange, 401, "No autorizado. Inicia sesion primero.");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);
        
        // Extraemos los Query Parameters de la URL (?cursoId=1&estudianteId=4)
        Map<String, String> queryParams = HttpHelper.getQueryParams(exchange);
        
        try {
            // 1. GET /api/matriculas?cursoId=1 -> lista estudiantes de un curso
            if (path.equals("/api/matriculas") && method.equals("GET")) {
                if (!queryParams.containsKey("cursoId")) {
                    HttpHelper.sendError(exchange, 400, "Falta el parametro cursoId");
                    return;
                }
                int cursoId = Integer.parseInt(queryParams.get("cursoId"));
                handleGetByCurso(exchange, cursoId);
            } 
            // 2. POST /api/matriculas -> matricula un estudiante (solo admin)
            else if (path.equals("/api/matriculas") && method.equals("POST")) {
                // Validación de Rol: Solo ADMIN
                if (!"ADMIN".equalsIgnoreCase(solicitante.getRol())) { 
                    HttpHelper.sendError(exchange, 403, "Prohibido. Solo los administradores pueden matricular.");
                    return;
                }
                handleCreate(exchange);
            } 
            // 3. DELETE /api/matriculas?cursoId=1&estudianteId=4 -> desmatricula (solo admin)
            else if (path.equals("/api/matriculas") && method.equals("DELETE")) {
                
                if (!"ADMIN".equalsIgnoreCase(solicitante.getRol())) { 
                    HttpHelper.sendError(exchange, 403, "Prohibido. Solo los administradores pueden desmatricular.");
                    return;
                }
                
                if (!queryParams.containsKey("cursoId") || !queryParams.containsKey("estudianteId")) {
                    HttpHelper.sendError(exchange, 400, "Faltan los parametros cursoId o estudianteId");
                    return;
                }
                
                int cursoId = Integer.parseInt(queryParams.get("cursoId"));
                int estudianteId = Integer.parseInt(queryParams.get("estudianteId"));
                handleDelete(exchange, cursoId, estudianteId);
            } 
            else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada en matriculas");
            }
        } catch (NumberFormatException e) {
            HttpHelper.sendError(exchange, 400, "Los identificadores deben ser numericos validos");
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage()); 
        } catch (Exception e) {
            e.printStackTrace(); 
            HttpHelper.sendError(exchange, 500, "Error interno del servidor en matriculas");
        } finally {
            exchange.close(); 
        }
    }

    // GET /api/matriculas?cursoId=1
    private void handleGetByCurso(HttpExchange exchange, int cursoId) throws IOException {
        List<Matricula> matriculas = matriculaService.getByCurso(cursoId);
        
        List<Map<String, Object>> lista = matriculas.stream()
                .map(Matricula::toMap)
                .collect(Collectors.toList());
                
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }

    // POST /api/matriculas
    private void handleCreate(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        
        if (body == null || body.isEmpty()) {
            HttpHelper.sendError(exchange, 400, "El cuerpo de la peticion JSON esta vacio");
            return;
        }

        Number cursoObj = (Number) body.get("cursoId");
        Number estObj = (Number) body.get("estudianteId");
        String fecha = (String) body.get("fecha"); // Opcional, puedes generarla en el servicio si no viene

        int cursoId = (cursoObj != null) ? cursoObj.intValue() : 0;
        int estudianteId = (estObj != null) ? estObj.intValue() : 0;

        Matricula creada = matriculaService.create(cursoId, estudianteId, fecha);
        
        HttpHelper.sendJson(exchange, 201, creada.toMap());
    }

    // DELETE /api/matriculas?cursoId=1&estudianteId=4
    private void handleDelete(HttpExchange exchange, int cursoId, int estudianteId) throws IOException {
        // Ajustamos la llamada al servicio pasando ambos identificadores obligatorios
        boolean eliminado = matriculaService.cancelarMatricula(cursoId, estudianteId);
        
        if (!eliminado) {
            HttpHelper.sendError(exchange, 404, "Matricula no encontrada con los datos provistos");
            return;
        }
        
        HttpHelper.sendJson(exchange, 200, Map.of("mensaje", "Matricula cancelada correctamente"));
    }
}
