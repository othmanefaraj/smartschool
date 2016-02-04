package edu.smartschool.repository.search;

import edu.smartschool.domain.Cycle;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Cycle entity.
 */
public interface CycleSearchRepository extends ElasticsearchRepository<Cycle, Long> {
}
