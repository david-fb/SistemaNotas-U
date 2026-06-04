package service;

import model.Curso;
import repository.CursoRepository;

import java.util.List;

public class CursoService {

    
    private final CursoRepository cursoRepository = new CursoRepository();

    // GET /api/cursos
    public List<Curso> getAll() {
        return cursoRepository.findAll();
    }
}