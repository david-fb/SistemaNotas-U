
package repository;
import model.Matricula;
import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class MatriculaRepository {
    
    public List<Matricula> findByCurso(int cursoId) {
        List<Matricula> matriculas = new ArrayList<>();
        String sql = "SELECT id, curso_id, estudiante_id, fecha, estado FROM public.matricula WHERE curso_id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cursoId); // 

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { 
                    matriculas.add(new Matricula(
                            rs.getInt("id"),
                            rs.getInt("curso_id"),
                            rs.getInt("estudiante_id"),
                            rs.getString("fecha"),
                            rs.getBoolean("estado")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matriculas; // Retorna la lista (estará vacía si el curso no tiene materias)
    }
    public Matricula save(Matricula matricula) {
        String sql = "INSERT INTO public.matricula (curso_id, estudiante_id, fecha, estado) VALUES (?, ?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula.getCursoId());
            stmt.setInt(2, matricula.getEstudianteId());
            stmt.setString(3, matricula.getFecha());
            stmt.setBoolean(4, true);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Matricula(
                            rs.getInt("id"),
                            rs.getInt("curso_id"),
                            rs.getInt("estudiante_id"),
                            rs.getString("fecha"),
                            rs.getBoolean("estado")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear matricula en repositorio", e);
        }
    }
    public boolean deleteLogico(int id) {
    // Cambia el estado a false para simular la eliminación (Borrado Lógico)
    String sql = "UPDATE public.matricula SET estado = false WHERE id = ?";

    try (Connection conn = DatabaseConfig.getConnection(); 
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        int filasAfectadas = ps.executeUpdate();
        
        return filasAfectadas > 0; // Retorna true si encontró la matrícula y la actualizó

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error al dar de baja la matricula en el repositorio", e);
    }
}

    
}
