package edu.smartschool.repository;

import edu.smartschool.domain.ClasseType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ClasseType entity.
 */
public interface ClasseTypeRepository extends JpaRepository<ClasseType,Long> {

}
