/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Corte;

public class CorteRepository {
    
    private Corte mapRow(ResultSet rs) throws SQLException {
        return new Corte(
            rs.getInt("id"),
            rs.getInt("curso_id"),
            rs.getDouble("porcentaje")
        );
    }
    
    public List<Corte> findByCurso(int cursoId){
        String sql = "select * from corte where curso_id = ? ORDER BY id ASC";
        List<Corte> cortes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cortes.add(mapRow(rs));
            }
            return cortes;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el corte por curso", e);
        }
    }
    
    public Corte findById(int corteId){
        
        String sql = "select * from corte where id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, corteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el corte por ID", e);
        }
    }
    
    // Suma total de porcentajes de los cortes de un curso
    // Se usa para validar que no supere 100%
    public double sumaPorcentajes(int cursoId) {
        String sql = "select coalesce(SUM(porcentaje), 0) from corte where curso_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar porcentajes", e);
        }
    }
    
    // Insertar un nuevo corte y retornar el objeto creado
    public Corte save(Corte corte) {
        String sql = "insert into corte (curso_id, porcentaje) values (?, ?) returning *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, corte.getCursoId());
            stmt.setDouble(2, corte.getPorcentaje());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar corte", e);
        }
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM corte WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar corte", e);
        }
    }
}
