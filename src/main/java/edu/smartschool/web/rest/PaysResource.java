package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Pays;
import edu.smartschool.repository.PaysRepository;
import edu.smartschool.repository.search.PaysSearchRepository;
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
 * REST controller for managing Pays.
 */
@RestController
@RequestMapping("/api")
public class PaysResource {

    private final Logger log = LoggerFactory.getLogger(PaysResource.class);
        
    @Inject
    private PaysRepository paysRepository;
    
    @Inject
    private PaysSearchRepository paysSearchRepository;
    
    /**
     * POST  /payss -> Create a new pays.
     */
    @RequestMapping(value = "/payss",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pays> createPays(@Valid @RequestBody Pays pays) throws URISyntaxException {
        log.debug("REST request to save Pays : {}", pays);
        if (pays.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pays", "idexists", "A new pays cannot already have an ID")).body(null);
        }
        Pays result = paysRepository.save(pays);
        paysSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/payss/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("pays", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /payss -> Updates an existing pays.
     */
    @RequestMapping(value = "/payss",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pays> updatePays(@Valid @RequestBody Pays pays) throws URISyntaxException {
        log.debug("REST request to update Pays : {}", pays);
        if (pays.getId() == null) {
            return createPays(pays);
        }
        Pays result = paysRepository.save(pays);
        paysSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("pays", pays.getId().toString()))
            .body(result);
    }

    /**
     * GET  /payss -> get all the payss.
     */
    @RequestMapping(value = "/payss",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pays>> getAllPayss(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Payss");
        Page<Pays> page = paysRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/payss");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /payss/:id -> get the "id" pays.
     */
    @RequestMapping(value = "/payss/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pays> getPays(@PathVariable Long id) {
        log.debug("REST request to get Pays : {}", id);
        Pays pays = paysRepository.findOne(id);
        return Optional.ofNullable(pays)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /payss/:id -> delete the "id" pays.
     */
    @RequestMapping(value = "/payss/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePays(@PathVariable Long id) {
        log.debug("REST request to delete Pays : {}", id);
        paysRepository.delete(id);
        paysSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("pays", id.toString())).build();
    }

    /**
     * SEARCH  /_search/payss/:query -> search for the pays corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/payss/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Pays> searchPayss(@PathVariable String query) {
        log.debug("REST request to search Payss for query {}", query);
        return StreamSupport
            .stream(paysSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
