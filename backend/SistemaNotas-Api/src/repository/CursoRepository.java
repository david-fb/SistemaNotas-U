package repository;

import model.Curso;
import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoRepository {

    public List<Curso> findAll() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT id, nombre, codigo, profesor_id, semestre_id FROM public.curso";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cursos.add(new Curso(
                        rs.getInt("id"), rs.getString("nombre"), rs.getString("codigo"),
                        rs.getInt("profesor_id"), rs.getInt("semestre_id")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursos;
    }

    public Curso save(Curso curso) {
        String sql = "INSERT INTO public.curso (nombre, codigo, profesor_id, semestre_id) VALUES (?, ?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, curso.getNombre());
            stmt.setString(2, curso.getCodigo());
            stmt.setInt(3, curso.getProfesorId());
            stmt.setInt(4, curso.getSemestreId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Curso(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("codigo"),
                            rs.getInt("profesor_id"),
                            rs.getInt("semestre_id")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear curso en repositorio", e);
        }
    }

    public Curso findByCodigo(String codigo) {
        String sql = "SELECT id, nombre, codigo, profesor_id, semestre_id FROM public.curso WHERE codigo = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Curso(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("codigo"),
                            rs.getInt("profesor_id"),
                            rs.getInt("semestre_id")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Si no lo encuentra, retorna null de forma segura
    }
    

    public List<Curso> findByProfesor(int profesorId) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT id, nombre, codigo, profesor_id, semestre_id FROM public.curso WHERE profesor_id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, profesorId); // 

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { 
                    cursos.add(new Curso(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("codigo"),
                            rs.getInt("profesor_id"),
                            rs.getInt("semestre_id")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursos; // Retorna la lista (estará vacía si el profesor no tiene cursos)
    }

}
