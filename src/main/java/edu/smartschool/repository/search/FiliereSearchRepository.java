package edu.smartschool.repository.search;

import edu.smartschool.domain.Filiere;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Filiere entity.
 */
public interface FiliereSearchRepository extends ElasticsearchRepository<Filiere, Long> {
}
