package edu.smartschool.repository;

import edu.smartschool.domain.Filiere;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Filiere entity.
 */
public interface FiliereRepository extends JpaRepository<Filiere,Long> {

}
