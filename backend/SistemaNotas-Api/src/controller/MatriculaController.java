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

    // Instanciamos el servicio siguiendo el patrón de diseño por capas
    private final MatriculaService matriculaService = new MatriculaService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Manejo de CORS obligatorio para la conexión con el Frontend
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        // Validación obligatoria de seguridad (Filtro de sesión)
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (solicitante == null) {
            HttpHelper.sendError(exchange, 401, "No autorizado. Inicia sesion primero.");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);
        
        // Captura el ID si la URL contiene un parámetro en la cuarta posición: /api/matriculas/curso/{id}
        String idStr = HttpHelper.getPathParam(exchange, 3); 
        
        try {
            // 1. GET /api/matriculas/curso/{id} -> Listar alumnos de un curso
            if (path.matches("/api/matriculas/curso/\\d+") && method.equals("GET")) {
                handleGetByCurso(exchange, Integer.parseInt(idStr));
            } 
            // 2. POST /api/matriculas -> Crear una nueva matrícula
            else if (path.equals("/api/matriculas") && method.equals("POST")) {
                handleCreate(exchange);
            } 
            else if (path.matches("/api/matriculas/\\d+") && method.equals("DELETE")) {
                handleDelete(exchange, Integer.parseInt(idStr));
            } 
            // 4. Ruta no encontrada
            else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada en matriculas");
            }
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage()); // Captura los errores de validación de negocio
        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error real en la consola de NetBeans
            HttpHelper.sendError(exchange, 500, "Error interno del servidor en matriculas");
        } finally {
            exchange.close(); // Clausura obligatoria del stream de red para evitar el 'socket hang up'
        }
    }

    // GET /api/matriculas/curso/{id}
    private void handleGetByCurso(HttpExchange exchange, int cursoId) throws IOException {
        List<Matricula> matriculas = matriculaService.getByCurso(cursoId);
        
        // Convierte la lista de objetos Matricula a una lista de LinkedHashMaps usando toMap()
        List<Map<String, Object>> lista = matriculas.stream()
                .map(Matricula::toMap)
                .collect(Collectors.toList());
                
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }

    // POST /api/matriculas
    // Body JSON: { "cursoId": 3, "estudianteId": 5, "fecha": "2026-06-04" }
    private void handleCreate(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        
        if (body == null || body.isEmpty()) {
            HttpHelper.sendError(exchange, 400, "El cuerpo de la petición JSON está vacío");
            return;
        }

        // Extracción coordinada con el camelCase de tu modelo e intValue() seguro
        Number cursoObj = (Number) body.get("cursoId");
        Number estObj = (Number) body.get("estudianteId");
        String fecha = (String) body.get("fecha");

        int cursoId = (cursoObj != null) ? cursoObj.intValue() : 0;
        int estudianteId = (estObj != null) ? estObj.intValue() : 0;

        // Llama a la capa de negocio pasando los datos limpios
        Matricula creada = matriculaService.create(cursoId, estudianteId, fecha);
        
        HttpHelper.sendJson(exchange, 201, creada.toMap());
    }
    // DELETE /api/matriculas/{id}
    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        boolean eliminado = matriculaService.cancelarMatricula(id);
        
        if (!eliminado) {
            HttpHelper.sendError(exchange, 404, "Matrícula no encontrada");
            return;
        }
        
        // Retorna un mensaje de confirmación exitoso en formato JSON
        HttpHelper.sendJson(exchange, 200, Map.of("mensaje", "Matrícula cancelada correctamente"));
    }
}
