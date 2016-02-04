package edu.smartschool.repository.search;

import edu.smartschool.domain.Optionn;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Optionn entity.
 */
public interface OptionnSearchRepository extends ElasticsearchRepository<Optionn, Long> {
}
