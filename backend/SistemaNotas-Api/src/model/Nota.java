package model;

import java.util.Map;
import java.util.LinkedHashMap;

public class Nota {

    private int id;
    private int estudianteId;
    private int corteId;
    private double valor;

    public Nota() {
    }

    public Nota(int id, int estudianteId, int corteId, double valor) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.corteId = corteId;
        this.valor = valor;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("estudianteId", estudianteId);
        map.put("corteId", corteId);
        map.put("valor", valor);
        return map;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public int getCorteId() {
        return corteId;
    }

    public void setCorteId(int corteId) {
        this.corteId = corteId;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
