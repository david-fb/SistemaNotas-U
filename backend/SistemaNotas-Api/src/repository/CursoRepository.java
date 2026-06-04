
package repository;

import config.DatabaseConfig;
import model.Curso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoRepository {
    private Curso mapRow(ResultSet rs) throws SQLException {
        return new Curso(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("codigo"),
            rs.getInt("profesor_id"),
            rs.getInt("semestre_id")
            
        );
    }
    public Curso findById(int id) {
        String sql = "SELECT * FROM curso WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar curso por ID", e);
        }
    }
    public List<Curso> findAll() {
        String sql = "SELECT * FROM curso ORDER BY id";
        List<Curso> cursos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cursos.add(mapRow(rs));
            }
            return cursos;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cursos", e);
        }
    }
    public List<Curso> findByCodigo(String rol) {
        String sql = "SELECT * FROM curso WHERE codigo = ? AND activo = true ORDER BY nombre";
        List<Curso> cursos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cursos.add(mapRow(rs));
            }
            return cursos;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cursos por codigo", e);
        }
    }
     public Curso save(Curso curso) {
        String sql = "INSERT INTO usuario (nombre, correo, password, rol, activo) VALUES (?, ?, md5(?), ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, curso.getNombre());
            stmt.setString(2, curso.getCodigo());
            stmt.setInt(3, curso.getProfesorId());
            stmt.setInt(4, curso.getSemestreId());
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

}
