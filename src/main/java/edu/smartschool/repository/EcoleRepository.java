package edu.smartschool.repository;

import edu.smartschool.domain.Ecole;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Ecole entity.
 */
public interface EcoleRepository extends JpaRepository<Ecole,Long> {

    @Query("select ecole from Ecole ecole where ecole.user.login = ?#{principal.username}")
    List<Ecole> findByUserIsCurrentUser();

    @Query("select distinct ecole from Ecole ecole left join fetch ecole.classeTypes")
    List<Ecole> findAllWithEagerRelationships();

    @Query("select ecole from Ecole ecole left join fetch ecole.classeTypes where ecole.id =:id")
    Ecole findOneWithEagerRelationships(@Param("id") Long id);

}
