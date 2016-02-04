package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Niveau;
import edu.smartschool.repository.NiveauRepository;
import edu.smartschool.repository.search.NiveauSearchRepository;
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
 * REST controller for managing Niveau.
 */
@RestController
@RequestMapping("/api")
public class NiveauResource {

    private final Logger log = LoggerFactory.getLogger(NiveauResource.class);
        
    @Inject
    private NiveauRepository niveauRepository;
    
    @Inject
    private NiveauSearchRepository niveauSearchRepository;
    
    /**
     * POST  /niveaus -> Create a new niveau.
     */
    @RequestMapping(value = "/niveaus",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Niveau> createNiveau(@Valid @RequestBody Niveau niveau) throws URISyntaxException {
        log.debug("REST request to save Niveau : {}", niveau);
        if (niveau.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("niveau", "idexists", "A new niveau cannot already have an ID")).body(null);
        }
        Niveau result = niveauRepository.save(niveau);
        niveauSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/niveaus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("niveau", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /niveaus -> Updates an existing niveau.
     */
    @RequestMapping(value = "/niveaus",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Niveau> updateNiveau(@Valid @RequestBody Niveau niveau) throws URISyntaxException {
        log.debug("REST request to update Niveau : {}", niveau);
        if (niveau.getId() == null) {
            return createNiveau(niveau);
        }
        Niveau result = niveauRepository.save(niveau);
        niveauSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("niveau", niveau.getId().toString()))
            .body(result);
    }

    /**
     * GET  /niveaus -> get all the niveaus.
     */
    @RequestMapping(value = "/niveaus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Niveau>> getAllNiveaus(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Niveaus");
        Page<Niveau> page = niveauRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/niveaus");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /niveaus/:id -> get the "id" niveau.
     */
    @RequestMapping(value = "/niveaus/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Niveau> getNiveau(@PathVariable Long id) {
        log.debug("REST request to get Niveau : {}", id);
        Niveau niveau = niveauRepository.findOne(id);
        return Optional.ofNullable(niveau)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /niveaus/:id -> delete the "id" niveau.
     */
    @RequestMapping(value = "/niveaus/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNiveau(@PathVariable Long id) {
        log.debug("REST request to delete Niveau : {}", id);
        niveauRepository.delete(id);
        niveauSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("niveau", id.toString())).build();
    }

    /**
     * SEARCH  /_search/niveaus/:query -> search for the niveau corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/niveaus/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Niveau> searchNiveaus(@PathVariable String query) {
        log.debug("REST request to search Niveaus for query {}", query);
        return StreamSupport
            .stream(niveauSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
