package service;

import model.Usuario;
import repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    public Usuario getById(int id) {
        return usuarioRepository.findById(id);
    }

    public Usuario create(String nombre, String correo, String password, String rol) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (!List.of("admin", "profesor", "estudiante").contains(rol)) {
            throw new IllegalArgumentException("Rol inválido. Use: admin, profesor o estudiante");
        }
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        return usuarioRepository.save(new Usuario(0, nombre, correo, password, rol, true));
    }

    public Usuario update(int id, String nombre, String correo, String rol) {
        if (usuarioRepository.findById(id) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (!List.of("admin", "profesor", "estudiante").contains(rol)) {
            throw new IllegalArgumentException("Rol inválido. Use: admin, profesor o estudiante");
        }

        Usuario existente = usuarioRepository.findByCorreo(correo);
        if (existente != null && existente.getId() != id) {
            throw new IllegalArgumentException("Ese correo ya está en uso por otro usuario");
        }

        return usuarioRepository.update(new Usuario(id, nombre, correo, null, rol, true));
    }

    public boolean deactivate(int id) {
        if (usuarioRepository.findById(id) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return usuarioRepository.deactivate(id);
    }
}
