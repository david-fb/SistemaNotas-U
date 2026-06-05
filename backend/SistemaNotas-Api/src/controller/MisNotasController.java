/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Corte;
import model.Nota;
import model.Usuario;
import repository.CorteRepository;
import repository.NotaRepository;
import util.HttpHelper;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MisNotasController implements HttpHandler {

    private final NotaRepository notaRepository = new NotaRepository();
    private final CorteRepository corteRepository = new CorteRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = HttpHelper.getMethod(exchange);

        Usuario estudiante = HttpHelper.getUsuario(exchange);
        if (!estudiante.getRol().equals("estudiante")) {
            HttpHelper.sendError(exchange, 403, "Acceso denegado. Se requiere rol estudiante.");
            return;
        }

        try {
            if (path.equals("/api/mis-notas") && method.equals("GET")) {
                handleMisNotas(exchange, estudiante);
            } else if (path.equals("/api/mis-notas/promedio") && method.equals("GET")) {
                handlePromedio(exchange, estudiante);
            } else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada");
            }
        } catch (IllegalArgumentException e) {
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor");
        }
    }

    private void handleMisNotas(HttpExchange exchange, Usuario estudiante) throws IOException {
        int estudianteId = estudiante.getId();
        List<Nota> todasLasNotas = notaRepository.findByEstudiante(estudianteId);

        Map<Integer, List<Nota>> notasPorCurso = new LinkedHashMap<>();
        for (Nota nota : todasLasNotas) {
            Corte corte = corteRepository.findById(nota.getCorteId());
            if (corte != null) {
                notasPorCurso.computeIfAbsent(corte.getCursoId(), k -> new ArrayList<>()).add(nota);
            }
        }

        StringBuilder sb = new StringBuilder("[");
        int i = 0;
        for (Map.Entry<Integer, List<Nota>> entry : notasPorCurso.entrySet()) {
            if (i > 0) sb.append(",");
            List<Nota> notasDelCurso = entry.getValue();
            double definitiva = calcularDefinitiva(notasDelCurso);
            String estado = definitiva >= 3.0 ? "Aprobado" : "Reprobado";

            List<Map<String, Object>> notasMaps = notasDelCurso.stream()
                    .map(Nota::toMap).collect(Collectors.toList());

            sb.append("{");
            sb.append("\"cursoId\":").append(entry.getKey()).append(",");
            sb.append("\"notas\":").append(JsonUtil.toJsonArray(notasMaps)).append(",");
            sb.append("\"definitiva\":").append(Math.round(definitiva * 100.0) / 100.0).append(",");
            sb.append("\"estado\":\"").append(estado).append("\"");
            sb.append("}");
            i++;
        }
        sb.append("]");

        HttpHelper.sendJsonArray(exchange, 200, sb.toString());
    }

    private void handlePromedio(HttpExchange exchange, Usuario estudiante) throws IOException {
        int estudianteId = estudiante.getId();
        List<Nota> todasLasNotas = notaRepository.findByEstudiante(estudianteId);

        Map<Integer, List<Nota>> notasPorCurso = new LinkedHashMap<>();
        for (Nota nota : todasLasNotas) {
            Corte corte = corteRepository.findById(nota.getCorteId());
            if (corte != null) {
                notasPorCurso.computeIfAbsent(corte.getCursoId(), k -> new ArrayList<>()).add(nota);
            }
        }

        if (notasPorCurso.isEmpty()) {
            HttpHelper.sendJson(exchange, 200, Map.of("promedio", 0.0));
            return;
        }

        double sumaDefinitivas = 0;
        for (List<Nota> notasDelCurso : notasPorCurso.values()) {
            sumaDefinitivas += calcularDefinitiva(notasDelCurso);
        }

        double promedio = Math.round((sumaDefinitivas / notasPorCurso.size()) * 100.0) / 100.0;
        HttpHelper.sendJson(exchange, 200, Map.of("promedio", promedio));
    }

    private double calcularDefinitiva(List<Nota> notasDelCurso) {
        double definitiva = 0;
        for (Nota nota : notasDelCurso) {
            Corte corte = corteRepository.findById(nota.getCorteId());
            if (corte != null) {
                definitiva += nota.getValor() * corte.getPorcentaje() / 100;
            }
        }
        return definitiva;
    }
}
