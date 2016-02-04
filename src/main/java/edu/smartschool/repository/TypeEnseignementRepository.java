package edu.smartschool.repository;

import edu.smartschool.domain.TypeEnseignement;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TypeEnseignement entity.
 */
public interface TypeEnseignementRepository extends JpaRepository<TypeEnseignement,Long> {

}
