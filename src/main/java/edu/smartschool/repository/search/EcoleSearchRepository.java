package edu.smartschool.repository.search;

import edu.smartschool.domain.Ecole;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Ecole entity.
 */
public interface EcoleSearchRepository extends ElasticsearchRepository<Ecole, Long> {
}
