package model;

import java.util.Map;
import java.util.LinkedHashMap;

public class Curso {

    private int id;
    private String nombre;
    private String codigo;
    private int profesorId;
    private int semestreId;

    public Curso() {
    }

    public Curso(int id, String nombre, String codigo, int profesorId, int semestreId) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.profesorId = profesorId;
        this.semestreId = semestreId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("codigo", codigo);
        map.put("profesorId", profesorId);
        map.put("semestreId", semestreId);
        return map;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(int profesorId) {
        this.profesorId = profesorId;
    }

    public int getSemestreId() {
        return semestreId;
    }

    public void setSemestreId(int semestreId) {
        this.semestreId = semestreId;
    }
}
