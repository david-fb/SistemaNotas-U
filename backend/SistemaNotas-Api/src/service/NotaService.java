/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import model.Nota;
import repository.NotaRepository;
import repository.CorteRepository;
import model.Corte;

public class NotaService {

    private final NotaRepository notaRepository = new NotaRepository();
    private final CorteRepository corteRepository = new CorteRepository();

    public List<Nota> getByCurso(int cursoId) {
        return notaRepository.findByCurso(cursoId);
    }

    public List<Nota> getByEstudiante(int estudianteId) {
        return notaRepository.findByEstudiante(estudianteId);
    }

    public Nota getById(int id) {
        return notaRepository.findById(id);
    }

    // Crea una nota validando:
    // 1. valor entre 0.0 y 5.0
    // 2. el estudiante esté matriculado en el curso del corte
    public Nota create(int estudianteId, int corteId, double valor) {
        if (valor < 0.0 || valor > 5.0) {
            throw new IllegalArgumentException("El valor de la nota debe estar entre 0.0 y 5.0.");
        }

        Corte corte = corteRepository.findById(corteId);
        if (corte == null) {
            throw new IllegalArgumentException("El corte con ID " + corteId + " no existe.");
        }

        if (!notaRepository.estudianteMatriculado(estudianteId, corteId)) {
            throw new IllegalArgumentException("El estudiante no está matriculado en el curso de este corte.");
        }

        Nota nueva = new Nota();
        nueva.setEstudianteId(estudianteId);
        nueva.setCorteId(corteId);
        nueva.setValor(valor);
        return notaRepository.save(nueva);
    }

    // Actualiza el valor de una nota. Valida rango y existencia.
    public Nota update(int id, double valor) {
        if (valor < 0.0 || valor > 5.0) {
            throw new IllegalArgumentException("El valor de la nota debe estar entre 0.0 y 5.0.");
        }

        Nota nota = notaRepository.findById(id);
        if (nota == null) {
            throw new IllegalArgumentException("Nota no encontrada con ID: " + id);
        }

        nota.setValor(valor);
        return notaRepository.update(nota);
    }

    // Elimina una nota. Lanza excepción si no existe.
    public void delete(int id) {
        Nota nota = notaRepository.findById(id);
        if (nota == null) {
            throw new IllegalArgumentException("Nota no encontrada con ID: " + id);
        }
        notaRepository.delete(id);
    }
}
