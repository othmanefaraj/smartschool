package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Ecole;
import edu.smartschool.repository.EcoleRepository;
import edu.smartschool.repository.search.EcoleSearchRepository;
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
 * REST controller for managing Ecole.
 */
@RestController
@RequestMapping("/api")
public class EcoleResource {

    private final Logger log = LoggerFactory.getLogger(EcoleResource.class);
        
    @Inject
    private EcoleRepository ecoleRepository;
    
    @Inject
    private EcoleSearchRepository ecoleSearchRepository;
    
    /**
     * POST  /ecoles -> Create a new ecole.
     */
    @RequestMapping(value = "/ecoles",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ecole> createEcole(@Valid @RequestBody Ecole ecole) throws URISyntaxException {
        log.debug("REST request to save Ecole : {}", ecole);
        if (ecole.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ecole", "idexists", "A new ecole cannot already have an ID")).body(null);
        }
        Ecole result = ecoleRepository.save(ecole);
        ecoleSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ecoles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("ecole", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ecoles -> Updates an existing ecole.
     */
    @RequestMapping(value = "/ecoles",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ecole> updateEcole(@Valid @RequestBody Ecole ecole) throws URISyntaxException {
        log.debug("REST request to update Ecole : {}", ecole);
        if (ecole.getId() == null) {
            return createEcole(ecole);
        }
        Ecole result = ecoleRepository.save(ecole);
        ecoleSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("ecole", ecole.getId().toString()))
            .body(result);
    }

    /**
     * GET  /ecoles -> get all the ecoles.
     */
    @RequestMapping(value = "/ecoles",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ecole>> getAllEcoles(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Ecoles");
        Page<Ecole> page = ecoleRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ecoles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ecoles/:id -> get the "id" ecole.
     */
    @RequestMapping(value = "/ecoles/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ecole> getEcole(@PathVariable Long id) {
        log.debug("REST request to get Ecole : {}", id);
        Ecole ecole = ecoleRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(ecole)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /ecoles/:id -> delete the "id" ecole.
     */
    @RequestMapping(value = "/ecoles/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEcole(@PathVariable Long id) {
        log.debug("REST request to delete Ecole : {}", id);
        ecoleRepository.delete(id);
        ecoleSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ecole", id.toString())).build();
    }

    /**
     * SEARCH  /_search/ecoles/:query -> search for the ecole corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/ecoles/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Ecole> searchEcoles(@PathVariable String query) {
        log.debug("REST request to search Ecoles for query {}", query);
        return StreamSupport
            .stream(ecoleSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
