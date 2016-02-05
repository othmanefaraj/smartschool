package edu.smartschool.web.rest;

import com.codahale.metrics.annotation.Timed;
import edu.smartschool.domain.ClasseType;
import edu.smartschool.repository.ClasseTypeRepository;
import edu.smartschool.repository.search.ClasseTypeSearchRepository;
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
 * REST controller for managing ClasseType.
 */
@RestController
@RequestMapping("/api")
public class ClasseTypeResource {

    private final Logger log = LoggerFactory.getLogger(ClasseTypeResource.class);

    @Inject
    private ClasseTypeRepository classeTypeRepository;

    @Inject
    private ClasseTypeSearchRepository classeTypeSearchRepository;

    /**
     * POST  /classeTypes -> Create a new classeType.
     */
    @RequestMapping(value = "/classeTypes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClasseType> createClasseType(@RequestBody ClasseType classeType) throws URISyntaxException {
        log.debug("REST request to save ClasseType : {}", classeType);
        if (classeType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("classeType", "idexists", "A new classeType cannot already have an ID")).body(null);
        }
        if (classeTypeRepository.findOneByIntitule(classeType.getIntitule()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("ClasseType " + classeType.getIntitule() + "exists ( Intitule )", "Classe-Type "+classeType.getIntitule()+" already exists  ( Intitule )", "Classe Type (" + classeType.getIntitule() + ") already exists"))
                .body(null);
        }
        List<ClasseType> classeTypes = classeTypeRepository.findAll();
        for(ClasseType ct: classeTypes){
            log.debug("Chercher si la classe type {} existe", ct.getIntitule());
            if (classeType.getTypeEnseignement()==ct.getTypeEnseignement() &&
                classeType.getCycle()==ct.getCycle() &&
                classeType.getNiveau()==ct.getNiveau() &&
                classeType.getFiliere()==ct.getFiliere() &&
                classeType.getSerie()==ct.getSerie() &&
                classeType.getOptionn()==ct.getOptionn()){
                return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("ClasseType " + classeType.getIntitule() + "exists ( Combinaison )", "Classe-Type "+classeType.getIntitule()+" already exists ( Combinaison )", "Classe Type (" + classeType.getIntitule() + ") already exists"))
                    .body(null);
            }
        }

        ClasseType result = classeTypeRepository.save(classeType);

        classeTypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/classeTypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("classeType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /classeTypes -> Updates an existing classeType.
     */
    @RequestMapping(value = "/classeTypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClasseType> updateClasseType(@RequestBody ClasseType classeType) throws URISyntaxException {
        log.debug("REST request to update ClasseType : {}", classeType);
        if (classeType.getId() == null) {
            return createClasseType(classeType);
        }
        List<ClasseType> classeTypes = classeTypeRepository.findAll();
        for(ClasseType ct: classeTypes){
            log.debug("Chercher si la classe type {} existe", ct.getIntitule());
            if(classeType.getIntitule().equals(ct.getIntitule()) && classeType.getId() != ct.getId()){
                return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("ClasseType " + classeType.getIntitule() + "exists ( Intitule )", "Classe-Type "+classeType.getIntitule()+" already exists  ( Intitule )", "Classe Type (" + classeType.getIntitule() + ") already exists"))
                    .body(null);
            }
            if (classeType.getTypeEnseignement()==ct.getTypeEnseignement() &&
                classeType.getCycle()==ct.getCycle() &&
                classeType.getNiveau()==ct.getNiveau() &&
                classeType.getFiliere()==ct.getFiliere() &&
                classeType.getSerie()==ct.getSerie() &&
                classeType.getOptionn()==ct.getOptionn() &&
                classeType.getId() != ct.getId()){
                return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("ClasseType " + classeType.getIntitule() + "exists ( Combinaison )", "Classe-Type "+classeType.getIntitule()+" already exists ( Combinaison )", "Classe Type (" + classeType.getIntitule() + ") already exists"))
                    .body(null);
            }
        }
        ClasseType result = classeTypeRepository.save(classeType);
        classeTypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("classeType", classeType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /classeTypes -> get all the classeTypes.
     */
    @RequestMapping(value = "/classeTypes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ClasseType>> getAllClasseTypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ClasseTypes");
        Page<ClasseType> page = classeTypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/classeTypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /classeTypes/:id -> get the "id" classeType.
     */
    @RequestMapping(value = "/classeTypes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClasseType> getClasseType(@PathVariable Long id) {
        log.debug("REST request to get ClasseType : {}", id);
        ClasseType classeType = classeTypeRepository.findOne(id);
        return Optional.ofNullable(classeType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /classeTypes/:id -> delete the "id" classeType.
     */
    @RequestMapping(value = "/classeTypes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteClasseType(@PathVariable Long id) {
        log.debug("REST request to delete ClasseType : {}", id);
        classeTypeRepository.delete(id);
        classeTypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("classeType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/classeTypes/:query -> search for the classeType corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/classeTypes/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ClasseType> searchClasseTypes(@PathVariable String query) {
        log.debug("REST request to search ClasseTypes for query {}", query);
        return StreamSupport
            .stream(classeTypeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
