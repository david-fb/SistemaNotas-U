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
        // Manejo de CORS obligatorio
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);
        String idStr = HttpHelper.getPathParam(exchange, 3); // Asegúrate de que use el índice correcto (3) según tu enrutamiento

        try {
            // Validación obligatoria de autenticación (Token válido)
            model.Usuario solicitante = HttpHelper.getUsuario(exchange);
            System.out.println("Ruta recibida: " + path + " | Método: " + method + " | Rol: " + (solicitante != null ? solicitante.getRol() : "null"));
            
            if (solicitante == null) {
                HttpHelper.sendError(exchange, 401, "No autorizado. Inicia sesion primero.");
                return;
            }

            // 1. GET /api/cursos -> Lista todos (REQUERIMIENTO: Solo admin)
            if (path.equals("/api/cursos") && method.equals("GET")) {
                if (!solicitante.getRol().equals("admin")) {
                    HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol admin.");
                    return;
                }
                handleGetAll(exchange);
            } 
            
            // 2. GET /api/cursos/{id} -> Obtiene uno (REQUERIMIENTO: Todos los roles)
            else if (path.matches("/api/cursos/\\d+") && method.equals("GET")) {
                // No se pone ninguna restricción de rol aquí, ya que todos los roles tienen acceso
                handleGetById(exchange, Integer.parseInt(idStr.trim()));
            } 
            
            // 3. GET /api/mis-cursos -> Cursos del profesor (REQUERIMIENTO: Solo profesor)
            else if (path.equals("/api/mis-cursos") && method.equals("GET")) {
                if (!solicitante.getRol().equals("profesor")) {
                    HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol profesor.");
                    return;
                }
                handleGetByProfesor(exchange, solicitante);
            } 
            
            // 4. POST /api/cursos -> Crea curso (REQUERIMIENTO: Solo admin)
            else if (path.equals("/api/cursos") && method.equals("POST")) {
                if (!solicitante.getRol().equals("admin")) {
                    HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol admin.");
                    return;
                }
                handleCreate(exchange);
            } 
            
            // Ruta no encontrada
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

        // GET /api/mis-cursos
    private void handleGetByProfesor(HttpExchange exchange, model.Usuario profesor) throws IOException {
        // Ejecuta tu línea propuesta usando el ID del profesor logueado
        List<Curso> cursos = cursoService.getByProfesor(profesor.getId());
        
        // Mapea la lista de objetos Curso a una estructura de Maps compatible con tu serializador
        List<Map<String, Object>> lista = cursos.stream()
                .map(Curso::toMap)
                .collect(Collectors.toList());
                
        // Envía la respuesta exitosa con la colección ordenada
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }
        // GET /api/cursos/{id} -> Obtiene uno (Permitido para todos los roles)
    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        Curso curso = cursoService.getById(id);
        
        // Si el curso con ese ID no existe en PostgreSQL, respondemos 404
        if (curso == null) {
            HttpHelper.sendError(exchange, 404, "Curso no encontrado");
            return;
        }
        
        // Si existe, lo convertimos a su LinkedHashMap y lo enviamos como JSON (200 OK)
        HttpHelper.sendJson(exchange, 200, curso.toMap());
    }


}
