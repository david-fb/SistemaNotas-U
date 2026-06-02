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
        String sql = "select * from corte where curso_id = ?";
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
}
