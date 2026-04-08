/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import model.Usuario;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    // HashMap que guarda las sesiones activas: token -> usuario
    private static final Map<String, Usuario> sessions = new HashMap<>();

    // Crea una nueva sesion para un usuario y devuelve el token
    public static String createSession(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, usuario);
        return token;
    }

    // Obtiene el usuario asociado a un token
    // Retorna null si el token no existe o es invalido
    public static Usuario getUsuario(String token) {
        if (token == null || token.isEmpty()) return null;
        return sessions.get(token);
    }

    // Elimina una sesion (logout)
    public static void removeSession(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    // Verifica si un token es valido
    public static boolean isValid(String token) {
        return token != null && sessions.containsKey(token);
    }
}