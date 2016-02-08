package edu.smartschool.repository;

import edu.smartschool.domain.Pays;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Pays entity.
 */
public interface PaysRepository extends JpaRepository<Pays,Long> {

}
