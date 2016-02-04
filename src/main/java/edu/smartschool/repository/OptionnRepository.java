package edu.smartschool.repository;

import edu.smartschool.domain.Optionn;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Optionn entity.
 */
public interface OptionnRepository extends JpaRepository<Optionn,Long> {

}
