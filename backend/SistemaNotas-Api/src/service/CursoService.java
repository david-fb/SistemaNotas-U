package service;

import model.Curso;
import repository.CursoRepository;
import java.util.List;

public class CursoService {

    private final CursoRepository cursoRepository = new CursoRepository();

    public List<Curso> getAll() {
        return cursoRepository.findAll();
    }

    public Curso create(String nombre, String codigo, Integer profesorId, Integer semestreId) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del curso es obligatorio");
        }
        if (profesorId == null || profesorId <= 0) {
            throw new IllegalArgumentException("Debe asignar un profesor válido");
        }
        if (semestreId == null || semestreId <= 0) {
            throw new IllegalArgumentException("Debe asignar un semestre válido");
        }

        // VALIDACIÓN SEGURA:
        if (cursoRepository.findByCodigo(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un curso registrado con ese código");
        }

        return cursoRepository.save(new Curso(0, nombre, codigo, profesorId, semestreId));
    }
    // Método para obtener los cursos asignados a un profesor específico
    public List<Curso> getByProfesor(int profesorId) {
        // Retorna la lista que genera el repositorio
        return cursoRepository.findByProfesor(profesorId);
    }
        // GET /api/cursos/{id}
    public Curso getById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del curso proporcionado no es válido");
        }
        return cursoRepository.findById(id);
    }


}
