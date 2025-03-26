package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IDepartementService;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/departement")
@CrossOrigin(origins = "http://localhost:4200")
public class DepartementRestController {
    @Autowired
    IDepartementService departementService;
    @GetMapping("/retrieve-all-departements")
    public List<Departement> getDepartements() {
        List<Departement> listDepartements = departementService.retrieveAllDepartements();
        return listDepartements;
    }
}
