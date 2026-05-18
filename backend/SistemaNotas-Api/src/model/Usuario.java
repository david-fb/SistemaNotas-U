package model;

import java.util.Map;
import java.util.LinkedHashMap;

public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String password;
    private String rol;
    private boolean activo;

    public Usuario() {
    }

    // Referencia de patrón: todos los modelos deben tener toMap()
    // para poder serializarse con JsonUtil.toJson(model.toMap())
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("nombre", nombre);
        map.put("correo", correo);
        map.put("rol", rol);
        map.put("activo", activo);
        return map;
    }

    public Usuario(int id, String nombre, String correo, String password, String rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
