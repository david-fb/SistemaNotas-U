package service;

import model.Usuario;
import repository.UsuarioRepository;
import util.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    // Intenta autenticar al usuario con correo y password
    // Retorna un Map con el token y datos del usuario, o null si falla
    public Map<String, Object> login(String correo, String password) {
        // Validar que los campos no esten vacios
        if (correo == null || correo.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        // Buscar usuario en la BD (la comparacion md5 se hace en el repository)
        Usuario usuario = usuarioRepository.findByCorreoAndPassword(correo, password);

        if (usuario == null) {
            return null;
        }

        // Crear sesion y obtener token
        String token = SessionManager.createSession(usuario);

        // Armar la respuesta con token y datos del usuario (sin password)
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", usuario.getId());
        response.put("nombre", usuario.getNombre());
        response.put("correo", usuario.getCorreo());
        response.put("rol", usuario.getRol());

        return response;
    }

    // Cierra la sesion eliminando el token
    public void logout(String token) {
        SessionManager.removeSession(token);
    }
}