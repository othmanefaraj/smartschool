package edu.smartschool.repository;

import edu.smartschool.domain.Ville;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ville entity.
 */
public interface VilleRepository extends JpaRepository<Ville,Long> {

}
