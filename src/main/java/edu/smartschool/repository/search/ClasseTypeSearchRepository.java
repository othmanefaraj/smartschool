package edu.smartschool.repository.search;

import edu.smartschool.domain.ClasseType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ClasseType entity.
 */
public interface ClasseTypeSearchRepository extends ElasticsearchRepository<ClasseType, Long> {
}
