package edu.smartschool.repository.search;

import edu.smartschool.domain.Niveau;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Niveau entity.
 */
public interface NiveauSearchRepository extends ElasticsearchRepository<Niveau, Long> {
}
