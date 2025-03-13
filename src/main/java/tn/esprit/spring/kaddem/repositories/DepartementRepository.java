package tn.esprit.spring.kaddem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.kaddem.entities.Departement;

import java.util.List;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    @Query("SELECT d FROM Departement d WHERE " +
            "(:nomDepart IS NULL OR d.nomDepart LIKE %:nomDepart%) AND " +
            "(:minEtudiants IS NULL OR SIZE(d.etudiants) >= :minEtudiants)")
    List<Departement> filterDepartements(
            @Param("nomDepart") String nomDepart,
            @Param("minEtudiants") Integer minEtudiants
    );

}

