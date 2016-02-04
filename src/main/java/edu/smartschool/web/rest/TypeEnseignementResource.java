package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.TypeEnseignement;
import edu.smartschool.repository.TypeEnseignementRepository;
import edu.smartschool.repository.search.TypeEnseignementSearchRepository;
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
 * REST controller for managing TypeEnseignement.
 */
@RestController
@RequestMapping("/api")
public class TypeEnseignementResource {

    private final Logger log = LoggerFactory.getLogger(TypeEnseignementResource.class);
        
    @Inject
    private TypeEnseignementRepository typeEnseignementRepository;
    
    @Inject
    private TypeEnseignementSearchRepository typeEnseignementSearchRepository;
    
    /**
     * POST  /typeEnseignements -> Create a new typeEnseignement.
     */
    @RequestMapping(value = "/typeEnseignements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeEnseignement> createTypeEnseignement(@Valid @RequestBody TypeEnseignement typeEnseignement) throws URISyntaxException {
        log.debug("REST request to save TypeEnseignement : {}", typeEnseignement);
        if (typeEnseignement.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("typeEnseignement", "idexists", "A new typeEnseignement cannot already have an ID")).body(null);
        }
        TypeEnseignement result = typeEnseignementRepository.save(typeEnseignement);
        typeEnseignementSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/typeEnseignements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("typeEnseignement", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /typeEnseignements -> Updates an existing typeEnseignement.
     */
    @RequestMapping(value = "/typeEnseignements",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeEnseignement> updateTypeEnseignement(@Valid @RequestBody TypeEnseignement typeEnseignement) throws URISyntaxException {
        log.debug("REST request to update TypeEnseignement : {}", typeEnseignement);
        if (typeEnseignement.getId() == null) {
            return createTypeEnseignement(typeEnseignement);
        }
        TypeEnseignement result = typeEnseignementRepository.save(typeEnseignement);
        typeEnseignementSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("typeEnseignement", typeEnseignement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /typeEnseignements -> get all the typeEnseignements.
     */
    @RequestMapping(value = "/typeEnseignements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TypeEnseignement>> getAllTypeEnseignements(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of TypeEnseignements");
        Page<TypeEnseignement> page = typeEnseignementRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/typeEnseignements");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /typeEnseignements/:id -> get the "id" typeEnseignement.
     */
    @RequestMapping(value = "/typeEnseignements/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeEnseignement> getTypeEnseignement(@PathVariable Long id) {
        log.debug("REST request to get TypeEnseignement : {}", id);
        TypeEnseignement typeEnseignement = typeEnseignementRepository.findOne(id);
        return Optional.ofNullable(typeEnseignement)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /typeEnseignements/:id -> delete the "id" typeEnseignement.
     */
    @RequestMapping(value = "/typeEnseignements/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTypeEnseignement(@PathVariable Long id) {
        log.debug("REST request to delete TypeEnseignement : {}", id);
        typeEnseignementRepository.delete(id);
        typeEnseignementSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("typeEnseignement", id.toString())).build();
    }

    /**
     * SEARCH  /_search/typeEnseignements/:query -> search for the typeEnseignement corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/typeEnseignements/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TypeEnseignement> searchTypeEnseignements(@PathVariable String query) {
        log.debug("REST request to search TypeEnseignements for query {}", query);
        return StreamSupport
            .stream(typeEnseignementSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
