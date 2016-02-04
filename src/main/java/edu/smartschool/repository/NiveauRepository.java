package edu.smartschool.repository;

import edu.smartschool.domain.Niveau;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Niveau entity.
 */
public interface NiveauRepository extends JpaRepository<Niveau,Long> {

}
