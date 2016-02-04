package edu.smartschool.repository;

import edu.smartschool.domain.ClasseType;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA repository for the ClasseType entity.
 */
public interface ClasseTypeRepository extends JpaRepository<ClasseType,Long> {

    Optional<ClasseType> findOneByIntitule(String intitule);



}
