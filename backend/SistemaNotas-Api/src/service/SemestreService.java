package service;

import model.Semestre;
import repository.SemestreRepository;
import java.util.List;

public class SemestreService {

    private final SemestreRepository semestreRepository = new SemestreRepository();

    // Obtener todos los semestres
    public List<Semestre> getAll() {
        return semestreRepository.findAll();
    }

    // Crear un nuevo semestre con validaciones
    public Semestre create(String nombre, String fechaInicio, String fechaFin) {
        // Validación: nombre obligatorio
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del semestre es obligatorio");
        }

        // Validación: fecha inicio obligatoria
        if (fechaInicio == null || fechaInicio.isBlank()) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }

        // Validación: fecha fin obligatoria
        if (fechaFin == null || fechaFin.isBlank()) {
            throw new IllegalArgumentException("La fecha de fin es obligatoria");
        }

        return semestreRepository.save(new Semestre(0, nombre, fechaInicio, fechaFin));
    }
}
