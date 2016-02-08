package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Ville;
import edu.smartschool.repository.VilleRepository;
import edu.smartschool.repository.search.VilleSearchRepository;
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
 * REST controller for managing Ville.
 */
@RestController
@RequestMapping("/api")
public class VilleResource {

    private final Logger log = LoggerFactory.getLogger(VilleResource.class);
        
    @Inject
    private VilleRepository villeRepository;
    
    @Inject
    private VilleSearchRepository villeSearchRepository;
    
    /**
     * POST  /villes -> Create a new ville.
     */
    @RequestMapping(value = "/villes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ville> createVille(@Valid @RequestBody Ville ville) throws URISyntaxException {
        log.debug("REST request to save Ville : {}", ville);
        if (ville.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ville", "idexists", "A new ville cannot already have an ID")).body(null);
        }
        Ville result = villeRepository.save(ville);
        villeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/villes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("ville", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /villes -> Updates an existing ville.
     */
    @RequestMapping(value = "/villes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ville> updateVille(@Valid @RequestBody Ville ville) throws URISyntaxException {
        log.debug("REST request to update Ville : {}", ville);
        if (ville.getId() == null) {
            return createVille(ville);
        }
        Ville result = villeRepository.save(ville);
        villeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("ville", ville.getId().toString()))
            .body(result);
    }

    /**
     * GET  /villes -> get all the villes.
     */
    @RequestMapping(value = "/villes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ville>> getAllVilles(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Villes");
        Page<Ville> page = villeRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/villes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /villes/:id -> get the "id" ville.
     */
    @RequestMapping(value = "/villes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ville> getVille(@PathVariable Long id) {
        log.debug("REST request to get Ville : {}", id);
        Ville ville = villeRepository.findOne(id);
        return Optional.ofNullable(ville)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /villes/:id -> delete the "id" ville.
     */
    @RequestMapping(value = "/villes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteVille(@PathVariable Long id) {
        log.debug("REST request to delete Ville : {}", id);
        villeRepository.delete(id);
        villeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ville", id.toString())).build();
    }

    /**
     * SEARCH  /_search/villes/:query -> search for the ville corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/villes/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Ville> searchVilles(@PathVariable String query) {
        log.debug("REST request to search Villes for query {}", query);
        return StreamSupport
            .stream(villeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
