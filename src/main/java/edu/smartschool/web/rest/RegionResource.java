package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Region;
import edu.smartschool.repository.RegionRepository;
import edu.smartschool.repository.search.RegionSearchRepository;
import edu.smartschool.web.rest.util.HeaderUtil;
import edu.smartschool.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Region.
 */
@RestController
@RequestMapping("/api")
public class RegionResource {

    private final Logger log = LoggerFactory.getLogger(RegionResource.class);
        
    @Inject
    private RegionRepository regionRepository;
    
    @Inject
    private RegionSearchRepository regionSearchRepository;
    
    /**
     * POST  /regions -> Create a new region.
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Region> createRegion(@Valid @RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to save Region : {}", region);
        if (region.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("region", "idexists", "A new region cannot already have an ID")).body(null);
        }
        Region result = regionRepository.save(region);
        regionSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/regions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("region", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /regions -> Updates an existing region.
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Region> updateRegion(@Valid @RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to update Region : {}", region);
        if (region.getId() == null) {
            return createRegion(region);
        }
        Region result = regionRepository.save(region);
        regionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("region", region.getId().toString()))
            .body(result);
    }

    /**
     * GET  /regions -> get all the regions.
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Region>> getAllRegions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Regions");
        Page<Region> page = regionRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/regions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /regions/:id -> get the "id" region.
     */
    @RequestMapping(value = "/regions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Region> getRegion(@PathVariable Long id) {
        log.debug("REST request to get Region : {}", id);
        Region region = regionRepository.findOne(id);
        return Optional.ofNullable(region)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /regions/:id -> delete the "id" region.
     */
    @RequestMapping(value = "/regions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        log.debug("REST request to delete Region : {}", id);
        regionRepository.delete(id);
        regionSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("region", id.toString())).build();
    }

    /**
     * SEARCH  /_search/regions/:query -> search for the region corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/regions/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Region> searchRegions(@PathVariable String query) {
        log.debug("REST request to search Regions for query {}", query);
        return StreamSupport
            .stream(regionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
