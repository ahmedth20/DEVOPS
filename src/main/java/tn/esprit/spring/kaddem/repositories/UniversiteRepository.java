package tn.esprit.spring.kaddem.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.kaddem.entities.Universite;

import java.util.List;

@Repository
public interface UniversiteRepository extends JpaRepository<Universite,Integer> {

    @Query("SELECT u FROM Universite u WHERE u.budget >= :budget")
    List<Universite> findUniversitesByBudget(@Param("budget") double budget);

    @Query("SELECT u FROM Universite u WHERE u.anneeCreation >= :annee")
    List<Universite> findUniversitesByAnneeCreation(@Param("annee") int annee);

    Page<Universite> findAll(Pageable pageable);

    @Query("SELECT u FROM Universite u JOIN u.departements d JOIN d.etudiants e GROUP BY u HAVING COUNT(e) >= :minEtudiants")
    List<Universite> findUniversitesByMinEtudiants(@Param("minEtudiants") long minEtudiants);


}
