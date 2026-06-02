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
}
