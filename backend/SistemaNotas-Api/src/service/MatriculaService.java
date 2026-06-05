package service;

import model.Matricula;
import repository.MatriculaRepository;
import java.util.List;

public class MatriculaService {

    // Instanciamos el repositorio siguiendo tu patrón arquitectónico
    private final MatriculaRepository matriculaRepository = new MatriculaRepository();

    // GET /api/matriculas/curso/{id}
    public List<Matricula> getByCurso(int cursoId) {
        if (cursoId <= 0) {
            throw new IllegalArgumentException("El ID del curso proporcionado no es válido");
        }
        return matriculaRepository.findByCurso(cursoId);
    }

    // POST /api/matriculas (Con validaciones de negocio)
    public Matricula create(int cursoId, int estudianteId, String fecha) {
        // 1. Validar que se haya seleccionado un curso
        if (cursoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un curso válido para la matrícula");
        }
        
        // 2. Validar que se haya seleccionado un estudiante
        if (estudianteId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un estudiante válido para la matrícula");
        }
        
        // 3. Validar que la fecha de inscripción no esté en blanco
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("La fecha de matrícula es obligatoria");
        }

        // Construimos el objeto matrícula (enviamos ID 0 para que PostgreSQL genere el serial)
        // El estado se define inicialmente como true (activa)
        Matricula nuevaMatricula = new Matricula(0, cursoId, estudianteId, fecha, true);

        // Pasamos el objeto limpio al repositorio para guardarlo en PostgreSQL
        return matriculaRepository.save(nuevaMatricula);
    }
    
    public boolean cancelarMatricula(int id) {
    if (id <= 0) {
        throw new IllegalArgumentException("El ID de la matrícula no es válido");
    }
    return matriculaRepository.deleteLogico(id);
}
}
