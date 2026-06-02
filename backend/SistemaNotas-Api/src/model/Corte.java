    package model;

import java.util.Map;
import java.util.LinkedHashMap;

public class Corte {

    private int id;
    private int cursoId;
    private double porcentaje;

    public Corte() {
    }

    public Corte(int id, int cursoId, double porcentaje) {
        this.id = id;
        this.cursoId = cursoId;
        this.porcentaje = porcentaje;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("cursoId", cursoId);
        map.put("porcentaje", porcentaje);
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

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
