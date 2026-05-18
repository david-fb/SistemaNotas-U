package model;

import java.util.Map;
import java.util.LinkedHashMap;

public class Matricula {

    private int id;
    private int cursoId;
    private int estudianteId;
    private String fecha;
    private boolean estado;

    public Matricula() {
    }

    public Matricula(int id, int cursoId, int estudianteId, String fecha, boolean estado) {
        this.id = id;
        this.cursoId = cursoId;
        this.estudianteId = estudianteId;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("cursoId", cursoId);
        map.put("estudianteId", estudianteId);
        map.put("fecha", fecha);
        map.put("estado", estado);
        return map;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
