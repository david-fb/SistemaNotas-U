/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import model.Corte;
import repository.CorteRepository;


public class CorteService {
    
    private final CorteRepository corteRepository = new CorteRepository();
    
    public Corte getById(int id) {
        return corteRepository.findById(id);
    }
    
    public List<Corte> getByCurso(int id) {
        return corteRepository.findByCurso(id);
    }
    
     // Crea un corte validando:
    // 1. porcentaje entre 1 y 100
    // 2. la suma total del curso no supere 100%
    public Corte create(int cursoId, double porcentaje) {
        if (porcentaje < 1 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 1 y 100.");
        }

        double sumaActual = corteRepository.sumaPorcentajes(cursoId);
        if (sumaActual + porcentaje > 100) {
            double disponible = 100 - sumaActual;
            throw new IllegalArgumentException(
                "El porcentaje excede el 100%. Solo quedan " + disponible + "% disponibles para este curso."
            );
        }

        Corte nuevo = new Corte();
        nuevo.setCursoId(cursoId);
        nuevo.setPorcentaje(porcentaje);
        return corteRepository.save(nuevo);
    }
    
    // Elimina un corte. Lanza excepción si no existe.
    public void delete(int id) {
        Corte corte = corteRepository.findById(id);
        if (corte == null) {
            throw new IllegalArgumentException("Corte no encontrado con ID: " + id);
        }
        corteRepository.delete(id);
    }
}
