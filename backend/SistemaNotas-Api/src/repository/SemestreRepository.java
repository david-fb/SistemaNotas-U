package repository;

import model.Semestre;
import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SemestreRepository {

    // Convierte una fila del ResultSet en un objeto Semestre
    private Semestre mapRow(ResultSet rs) throws SQLException {
        return new Semestre(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("fecha_inicio"),
            rs.getString("fecha_fin")
        );
    }

    // Obtener todos los semestres
    public List<Semestre> findAll() {
        List<Semestre> semestres = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_inicio, fecha_fin FROM semestre ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                semestres.add(mapRow(rs));
            }
            return semestres;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar semestres", e);
        }
    }

    // Crear un nuevo semestre
    public Semestre save(Semestre semestre) {
        String sql = "INSERT INTO semestre (nombre, fecha_inicio, fecha_fin) VALUES (?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, semestre.getNombre());
            stmt.setString(2, semestre.getFechaInicio());
            stmt.setString(3, semestre.getFechaFin());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear semestre", e);
        }
    }
}
