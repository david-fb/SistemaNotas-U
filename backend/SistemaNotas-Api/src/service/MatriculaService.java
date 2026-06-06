package service;

import model.Matricula;
import repository.MatriculaRepository;
import java.util.List;

public class MatriculaService {

    private final MatriculaRepository matriculaRepository = new MatriculaRepository();

    // GET /api/matriculas?cursoId=1
    public List<Matricula> getByCurso(int cursoId) {
        if (cursoId <= 0) {
            throw new IllegalArgumentException("El ID del curso proporcionado no es válido");
        }
        return matriculaRepository.findByCurso(cursoId);
    }

    // POST /api/matriculas
    public Matricula create(int cursoId, int estudianteId, String fecha) {
        if (cursoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un curso válido para la matrícula");
        }
        
        if (estudianteId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un estudiante válido para la matrícula");
        }
        
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("La fecha de matrícula es obligatoria");
        }

        Matricula nuevaMatricula = new Matricula(0, cursoId, estudianteId, fecha, true);

        return matriculaRepository.save(nuevaMatricula);
    }
    
    // Espera recibir cursoId y estudianteId por parametros
    public boolean cancelarMatricula(int cursoId, int estudianteId) {
        if (cursoId <= 0) {
            throw new IllegalArgumentException("El ID del curso no es válido");
        }
        if (estudianteId <= 0) {
            throw new IllegalArgumentException("El ID del estudiante no es válido");
        }
        
        // Llama al método delete en MatriculaRepository
        return matriculaRepository.delete(cursoId, estudianteId);
    }
}
