package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Filiere;
import edu.smartschool.repository.FiliereRepository;
import edu.smartschool.repository.search.FiliereSearchRepository;
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
 * REST controller for managing Filiere.
 */
@RestController
@RequestMapping("/api")
public class FiliereResource {

    private final Logger log = LoggerFactory.getLogger(FiliereResource.class);
        
    @Inject
    private FiliereRepository filiereRepository;
    
    @Inject
    private FiliereSearchRepository filiereSearchRepository;
    
    /**
     * POST  /filieres -> Create a new filiere.
     */
    @RequestMapping(value = "/filieres",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Filiere> createFiliere(@Valid @RequestBody Filiere filiere) throws URISyntaxException {
        log.debug("REST request to save Filiere : {}", filiere);
        if (filiere.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("filiere", "idexists", "A new filiere cannot already have an ID")).body(null);
        }
        Filiere result = filiereRepository.save(filiere);
        filiereSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/filieres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("filiere", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /filieres -> Updates an existing filiere.
     */
    @RequestMapping(value = "/filieres",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Filiere> updateFiliere(@Valid @RequestBody Filiere filiere) throws URISyntaxException {
        log.debug("REST request to update Filiere : {}", filiere);
        if (filiere.getId() == null) {
            return createFiliere(filiere);
        }
        Filiere result = filiereRepository.save(filiere);
        filiereSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("filiere", filiere.getId().toString()))
            .body(result);
    }

    /**
     * GET  /filieres -> get all the filieres.
     */
    @RequestMapping(value = "/filieres",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Filiere>> getAllFilieres(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Filieres");
        Page<Filiere> page = filiereRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/filieres");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /filieres/:id -> get the "id" filiere.
     */
    @RequestMapping(value = "/filieres/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Filiere> getFiliere(@PathVariable Long id) {
        log.debug("REST request to get Filiere : {}", id);
        Filiere filiere = filiereRepository.findOne(id);
        return Optional.ofNullable(filiere)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /filieres/:id -> delete the "id" filiere.
     */
    @RequestMapping(value = "/filieres/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFiliere(@PathVariable Long id) {
        log.debug("REST request to delete Filiere : {}", id);
        filiereRepository.delete(id);
        filiereSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("filiere", id.toString())).build();
    }

    /**
     * SEARCH  /_search/filieres/:query -> search for the filiere corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/filieres/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Filiere> searchFilieres(@PathVariable String query) {
        log.debug("REST request to search Filieres for query {}", query);
        return StreamSupport
            .stream(filiereSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
