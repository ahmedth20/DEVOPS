package tn.esprit.spring.kaddem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Option;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant,Integer> {
    public List<Etudiant> findEtudiantsByDepartement_IdDepart(Integer idDepart);
@Query("Select e From Etudiant e where e.nomE= :nomE and e.prenomE= :prenomE")
    public Etudiant findByNomEAndPrenomE(@Param("nomE") String nomE, @Param("prenomE") String prenomE);

    @Query("SELECT e FROM Etudiant e WHERE " +
            "(:nomE IS NULL OR e.nomE LIKE %:nomE%) AND " +
            "(:prenomE IS NULL OR e.prenomE LIKE %:prenomE%) AND " +
            "(:op IS NULL OR e.op = :op)")
    List<Etudiant> filterEtudiants(
            @Param("nomE") String nomE,
            @Param("prenomE") String prenomE,
            @Param("op") Option op
    );

}
