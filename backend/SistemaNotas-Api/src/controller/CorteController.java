/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Corte;
import util.HttpHelper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Usuario;
import service.CorteService;
import util.JsonUtil;

public class CorteController implements HttpHandler{
    
    private final CorteService corteService = new CorteService();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        // Solo administradores pueden gestionar usuarios
       /* Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (!solicitante.getRol().equals("admin")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol admin.");
            return;
        }*/

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);
        String idStr = HttpHelper.getPathParam(exchange, 3); // /api/usuarios/{id}
        String idCurso = HttpHelper.getPathParam(exchange, 4);

        try {
            if (path.matches("/api/cortes/\\d+") && method.equals("GET")) {
                handleGetById(exchange, Integer.parseInt(idStr));
            } else if (path.matches("/api/cortes/curso/\\d+") && method.equals("GET")){
                handleGetByCurso(exchange, Integer.parseInt(idCurso));
            }else if (path.equals("/api/cortes") && method.equals("POST")) {
                handleCreate(exchange);
            } else if (path.matches("/api/cortes/\\d+") && method.equals("DELETE")) {
                handleDelete(exchange, Integer.parseInt(idStr));
            }else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada");
            }
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor");
        }
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        Corte corte = corteService.getById(id);
        if (corte == null) {
            HttpHelper.sendError(exchange, 404, "Corte no encontrado");
            return;
        }
        HttpHelper.sendJson(exchange, 200, corte.toMap());
    }
    
    private void handleGetByCurso(HttpExchange exchange, int id) throws IOException {
        List<Corte> cortes = corteService.getByCurso(id);
        List<Map<String, Object>> lista = cortes.stream()
                .map(Corte::toMap)
                .collect(Collectors.toList());
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }   
    
    private void handleCreate(HttpExchange exchange) throws IOException {
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (!solicitante.getRol().equals("profesor") && !solicitante.getRol().equals("admin")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado.");
            return;
        }
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        int cursoId = ((Number) body.get("cursoId")).intValue();
        double porcentaje = ((Number) body.get("porcentaje")).doubleValue();
        Corte creado = corteService.create(cursoId, porcentaje);
        HttpHelper.sendJson(exchange, 201, creado.toMap());
    }
    
    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        Usuario solicitante = HttpHelper.getUsuario(exchange);
        if (!solicitante.getRol().equals("profesor") && !solicitante.getRol().equals("admin")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado.");
            return;
        }
        corteService.delete(id);
        HttpHelper.sendJson(exchange, 200, Map.of("mensaje", "Corte eliminado"));
    }
}