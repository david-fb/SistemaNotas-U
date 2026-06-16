
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
        String sql = "SELECT m.id, m.curso_id, m.estudiante_id, m.fecha, m.estado, u.nombre, u.correo " +
                "FROM public.matricula m " +
                "JOIN public.usuario u ON u.id = m.estudiante_id " +
                "WHERE m.curso_id = ?   ";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cursoId); //

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    matriculas.add(new Matricula(
                            rs.getInt("id"),
                            rs.getInt("curso_id"),
                            rs.getInt("estudiante_id"),
                            rs.getString("fecha"),
                            rs.getBoolean("estado"),
                            rs.getString("nombre"),
                            rs.getString("correo")
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
            if ("23505".equals(e.getSQLState())) {
            throw new RuntimeException("El estudiante ya se encuentra registrado en este curso.");
        }
            e.printStackTrace();
            throw new RuntimeException("Error al crear matricula en repositorio", e);
        }
        
    }
    public boolean delete(int cursoId, int estudianteId) {
        
        String sql = "DELETE FROM public.matricula \n" +
            "WHERE curso_id = ? AND estudiante_id = ?;";

        try (Connection conn = DatabaseConfig.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cursoId);
            ps.setInt(2, estudianteId);
            int filasAfectadas = ps.executeUpdate();
            
            return filasAfectadas > 0; 

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al dar de baja la matricula en el repositorio", e);
        }
    }

    
}
