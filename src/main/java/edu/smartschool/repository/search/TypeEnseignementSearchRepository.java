package edu.smartschool.repository.search;

import edu.smartschool.domain.TypeEnseignement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TypeEnseignement entity.
 */
public interface TypeEnseignementSearchRepository extends ElasticsearchRepository<TypeEnseignement, Long> {
}
