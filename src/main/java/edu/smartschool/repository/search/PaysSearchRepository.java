package edu.smartschool.repository.search;

import edu.smartschool.domain.Pays;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Pays entity.
 */
public interface PaysSearchRepository extends ElasticsearchRepository<Pays, Long> {
}
