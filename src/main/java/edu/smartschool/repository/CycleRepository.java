package edu.smartschool.repository;

import edu.smartschool.domain.Cycle;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Cycle entity.
 */
public interface CycleRepository extends JpaRepository<Cycle,Long> {

}
