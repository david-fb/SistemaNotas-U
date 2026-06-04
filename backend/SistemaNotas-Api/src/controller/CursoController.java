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
            model.Usuario solicitante = HttpHelper.getUsuario(exchange);
            System.out.println("Ruta recibida: " + path);
            System.out.println("Método recibido: " + method);
            
            if (solicitante == null) {
                HttpHelper.sendError(exchange, 401, "No autorizado. Inicia sesion primero.");
                return;
            }
            
            // 1. Condicional para listar cursos
            if (path.equals("/api/cursos") && method.equals("GET")) {
                handleGetAll(exchange);
            } 
            // 2. ¡ESTA ES LA LÍNEA QUE FALTA AGREGAR! -> Conectar con handleCreate
            else if (path.equals("/api/cursos") && method.equals("POST")) {
                handleCreate(exchange);
            } 
            // 3. Si no es ninguna, da 404
            else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada en cursos");
            }
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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

    private void handleCreate(HttpExchange exchange) throws IOException {
    try {
        // 1. Leer el cuerpo JSON de forma segura
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        
        if (body == null || body.isEmpty()) {
            HttpHelper.sendError(exchange, 400, "El cuerpo JSON está vacío");
            return;
        }

        // 2. Extraer parámetros con validación de tipos
        String nombre = (String) body.get("nombre");
        String codigo = (String) body.get("codigo");
        
        Integer profesorId = null;
        Integer semestreId = null;

        if (body.get("profesorId") != null) {
            Object profObj = body.get("profesorId");
            profesorId = (profObj instanceof Number) ? ((Number) profObj).intValue() : Integer.parseInt(profObj.toString().trim());
        }

        if (body.get("semestreId") != null) {
            Object semObj = body.get("semestreId");
            semestreId = (semObj instanceof Number) ? ((Number) semObj).intValue() : Integer.parseInt(semObj.toString().trim());
        }

        System.out.println("Enviando al Service -> Nombre: " + nombre + ", Código: " + codigo);

        // 3. Crear el curso (Llamada al Service)
        Curso creado = cursoService.create(nombre, codigo, profesorId, semestreId);
        
        // 4. Enviar la respuesta exitosa y cerrar explícitamente el canal
        if (creado != null) {
            HttpHelper.sendJson(exchange, 201, creado.toMap());
        } else {
            HttpHelper.sendError(exchange, 500, "No se pudo recuperar el curso creado");
        }

    } catch (IllegalArgumentException e) {
        System.out.println("Validación rechazada: " + e.getMessage());
        HttpHelper.sendError(exchange, 400, e.getMessage());
    } catch (Exception e) {
        System.out.println("Excepción crítica en handleCreate:");
        e.printStackTrace();
        HttpHelper.sendError(exchange, 500, "Error interno del servidor en cursos");
    } finally {
        // Clausura obligatoria del stream de intercambio de red para liberar el socket
        exchange.close();
    }
}


}
