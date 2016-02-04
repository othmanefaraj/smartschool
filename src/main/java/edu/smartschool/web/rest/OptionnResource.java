package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.Optionn;
import edu.smartschool.repository.OptionnRepository;
import edu.smartschool.repository.search.OptionnSearchRepository;
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
 * REST controller for managing Optionn.
 */
@RestController
@RequestMapping("/api")
public class OptionnResource {

    private final Logger log = LoggerFactory.getLogger(OptionnResource.class);
        
    @Inject
    private OptionnRepository optionnRepository;
    
    @Inject
    private OptionnSearchRepository optionnSearchRepository;
    
    /**
     * POST  /optionns -> Create a new optionn.
     */
    @RequestMapping(value = "/optionns",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Optionn> createOptionn(@Valid @RequestBody Optionn optionn) throws URISyntaxException {
        log.debug("REST request to save Optionn : {}", optionn);
        if (optionn.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("optionn", "idexists", "A new optionn cannot already have an ID")).body(null);
        }
        Optionn result = optionnRepository.save(optionn);
        optionnSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/optionns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("optionn", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /optionns -> Updates an existing optionn.
     */
    @RequestMapping(value = "/optionns",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Optionn> updateOptionn(@Valid @RequestBody Optionn optionn) throws URISyntaxException {
        log.debug("REST request to update Optionn : {}", optionn);
        if (optionn.getId() == null) {
            return createOptionn(optionn);
        }
        Optionn result = optionnRepository.save(optionn);
        optionnSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("optionn", optionn.getId().toString()))
            .body(result);
    }

    /**
     * GET  /optionns -> get all the optionns.
     */
    @RequestMapping(value = "/optionns",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Optionn>> getAllOptionns(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Optionns");
        Page<Optionn> page = optionnRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/optionns");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /optionns/:id -> get the "id" optionn.
     */
    @RequestMapping(value = "/optionns/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Optionn> getOptionn(@PathVariable Long id) {
        log.debug("REST request to get Optionn : {}", id);
        Optionn optionn = optionnRepository.findOne(id);
        return Optional.ofNullable(optionn)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /optionns/:id -> delete the "id" optionn.
     */
    @RequestMapping(value = "/optionns/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOptionn(@PathVariable Long id) {
        log.debug("REST request to delete Optionn : {}", id);
        optionnRepository.delete(id);
        optionnSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("optionn", id.toString())).build();
    }

    /**
     * SEARCH  /_search/optionns/:query -> search for the optionn corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/optionns/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Optionn> searchOptionns(@PathVariable String query) {
        log.debug("REST request to search Optionns for query {}", query);
        return StreamSupport
            .stream(optionnSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
