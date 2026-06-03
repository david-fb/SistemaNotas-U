/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.Connection;
import config.DatabaseConfig;
import model.Nota;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotaRepository {
    
    private Nota mapRow(ResultSet rs) throws SQLException {
        return new Nota(
            rs.getInt("id"),
            rs.getInt("estudiante_id"),
            rs.getInt("corte_id"),
            rs.getDouble("valor")
        );
    }
    
    // Listar todas las notas de los estudiantes de un curso
    // Se hace JOIN con corte para filtrar por curso_id
    public List<Nota> findByCurso(int cursoId) {
        String sql = "SELECT n.* FROM nota n " +
                     "JOIN corte c ON n.corte_id = c.id " +
                     "WHERE c.curso_id = ? " +
                     "ORDER BY n.estudiante_id, n.corte_id";
        List<Nota> notas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notas.add(mapRow(rs));
            }
            return notas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar notas por curso", e);
        }
    }
    
    // Listar todas las notas de un estudiante (para mis-notas)
    public List<Nota> findByEstudiante(int estudianteId) {
        String sql = "SELECT * FROM nota WHERE estudiante_id = ? ORDER BY corte_id";
        List<Nota> notas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estudianteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notas.add(mapRow(rs));
            }
            return notas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar notas del estudiante", e);
        }
    }
    
    // Buscar una nota por ID
    public Nota findById(int id) {
        String sql = "SELECT * FROM nota WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar nota por ID", e);
        }
    }
    
    // Insertar una nueva nota y retornar el objeto creado
    public Nota save(Nota nota) {
        String sql = "INSERT INTO nota (estudiante_id, corte_id, valor) VALUES (?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nota.getEstudianteId());
            stmt.setInt(2, nota.getCorteId());
            stmt.setDouble(3, nota.getValor());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // violación de unicidad
                throw new IllegalArgumentException("Ya existe una nota para este estudiante en este corte.");
            }
            throw new RuntimeException("Error al guardar nota", e);
        }
    }
    
    // Actualizar el valor de una nota existente
    public Nota update(Nota nota) {
        String sql = "UPDATE nota SET valor = ? WHERE id = ? RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nota.getValor());
            stmt.setInt(2, nota.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar nota", e);
        }
    }

    // Eliminar una nota por ID
    public boolean delete(int id) {
        String sql = "DELETE FROM nota WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar nota", e);
        }
    }

    // Verificar si un estudiante está matriculado en el curso del corte
    // Se usa para validar antes de registrar una nota
    public boolean estudianteMatriculado(int estudianteId, int corteId) {
    String sql = "SELECT 1 FROM matricula m " +
                 "JOIN corte c ON m.curso_id = c.curso_id " +
                 "WHERE m.estudiante_id = ? AND c.id = ? AND m.estado = true";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estudianteId);
            stmt.setInt(2, corteId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar matrícula", e);
        }
    }
}
