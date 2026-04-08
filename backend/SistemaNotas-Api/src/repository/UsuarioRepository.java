package repository;

import config.DatabaseConfig;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    // Convierte una fila del ResultSet en un objeto Usuario
    private Usuario mapRow(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("correo"),
            rs.getString("password"),
            rs.getString("rol"),
            rs.getBoolean("activo")
        );
    }

    // Buscar usuario por correo y password (para login)
    // Usa md5() de PostgreSQL para comparar la contraseña
    public Usuario findByCorreoAndPassword(String correo, String password) {
        String sql = "SELECT * FROM usuario WHERE correo = ? AND password = md5(?) AND activo = true";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por credenciales", e);
        }
    }

    // Buscar usuario por ID
    public Usuario findById(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por ID", e);
        }
    }

    // Buscar usuario por correo (para validar unicidad)
    public Usuario findByCorreo(String correo) {
        String sql = "SELECT * FROM usuario WHERE correo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por correo", e);
        }
    }

    // Listar todos los usuarios
    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuario ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapRow(rs));
            }
            return usuarios;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }
    }

    // Listar usuarios por rol (para buscar estudiantes al matricular)
    public List<Usuario> findByRol(String rol) {
        String sql = "SELECT * FROM usuario WHERE rol = ? AND activo = true ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapRow(rs));
            }
            return usuarios;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios por rol", e);
        }
    }

    // Crear un nuevo usuario (password se guarda con md5)
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre, correo, password, rol, activo) VALUES (?, ?, md5(?), ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getRol());
            stmt.setBoolean(5, true);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    // Actualizar datos de un usuario (sin cambiar password)
    public Usuario update(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, correo = ?, rol = ? WHERE id = ? RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getRol());
            stmt.setInt(4, usuario.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    // Desactivar un usuario (no se borra, se marca como inactivo)
    public boolean deactivate(int id) {
        String sql = "UPDATE usuario SET activo = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al desactivar usuario", e);
        }
    }
}
