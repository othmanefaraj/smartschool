package edu.smartschool.web.rest;

import edu.smartschool.Application;
import edu.smartschool.domain.TypeEnseignement;
import edu.smartschool.repository.TypeEnseignementRepository;
import edu.smartschool.repository.search.TypeEnseignementSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TypeEnseignementResource REST controller.
 *
 * @see TypeEnseignementResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TypeEnseignementResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAA";
    private static final String UPDATED_INTITULE = "BBBBB";

    @Inject
    private TypeEnseignementRepository typeEnseignementRepository;

    @Inject
    private TypeEnseignementSearchRepository typeEnseignementSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTypeEnseignementMockMvc;

    private TypeEnseignement typeEnseignement;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeEnseignementResource typeEnseignementResource = new TypeEnseignementResource();
        ReflectionTestUtils.setField(typeEnseignementResource, "typeEnseignementSearchRepository", typeEnseignementSearchRepository);
        ReflectionTestUtils.setField(typeEnseignementResource, "typeEnseignementRepository", typeEnseignementRepository);
        this.restTypeEnseignementMockMvc = MockMvcBuilders.standaloneSetup(typeEnseignementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        typeEnseignement = new TypeEnseignement();
        typeEnseignement.setIntitule(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void createTypeEnseignement() throws Exception {
        int databaseSizeBeforeCreate = typeEnseignementRepository.findAll().size();

        // Create the TypeEnseignement

        restTypeEnseignementMockMvc.perform(post("/api/typeEnseignements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeEnseignement)))
                .andExpect(status().isCreated());

        // Validate the TypeEnseignement in the database
        List<TypeEnseignement> typeEnseignements = typeEnseignementRepository.findAll();
        assertThat(typeEnseignements).hasSize(databaseSizeBeforeCreate + 1);
        TypeEnseignement testTypeEnseignement = typeEnseignements.get(typeEnseignements.size() - 1);
        assertThat(testTypeEnseignement.getIntitule()).isEqualTo(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeEnseignementRepository.findAll().size();
        // set the field null
        typeEnseignement.setIntitule(null);

        // Create the TypeEnseignement, which fails.

        restTypeEnseignementMockMvc.perform(post("/api/typeEnseignements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeEnseignement)))
                .andExpect(status().isBadRequest());

        List<TypeEnseignement> typeEnseignements = typeEnseignementRepository.findAll();
        assertThat(typeEnseignements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTypeEnseignements() throws Exception {
        // Initialize the database
        typeEnseignementRepository.saveAndFlush(typeEnseignement);

        // Get all the typeEnseignements
        restTypeEnseignementMockMvc.perform(get("/api/typeEnseignements?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(typeEnseignement.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())));
    }

    @Test
    @Transactional
    public void getTypeEnseignement() throws Exception {
        // Initialize the database
        typeEnseignementRepository.saveAndFlush(typeEnseignement);

        // Get the typeEnseignement
        restTypeEnseignementMockMvc.perform(get("/api/typeEnseignements/{id}", typeEnseignement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(typeEnseignement.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTypeEnseignement() throws Exception {
        // Get the typeEnseignement
        restTypeEnseignementMockMvc.perform(get("/api/typeEnseignements/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTypeEnseignement() throws Exception {
        // Initialize the database
        typeEnseignementRepository.saveAndFlush(typeEnseignement);

		int databaseSizeBeforeUpdate = typeEnseignementRepository.findAll().size();

        // Update the typeEnseignement
        typeEnseignement.setIntitule(UPDATED_INTITULE);

        restTypeEnseignementMockMvc.perform(put("/api/typeEnseignements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeEnseignement)))
                .andExpect(status().isOk());

        // Validate the TypeEnseignement in the database
        List<TypeEnseignement> typeEnseignements = typeEnseignementRepository.findAll();
        assertThat(typeEnseignements).hasSize(databaseSizeBeforeUpdate);
        TypeEnseignement testTypeEnseignement = typeEnseignements.get(typeEnseignements.size() - 1);
        assertThat(testTypeEnseignement.getIntitule()).isEqualTo(UPDATED_INTITULE);
    }

    @Test
    @Transactional
    public void deleteTypeEnseignement() throws Exception {
        // Initialize the database
        typeEnseignementRepository.saveAndFlush(typeEnseignement);

		int databaseSizeBeforeDelete = typeEnseignementRepository.findAll().size();

        // Get the typeEnseignement
        restTypeEnseignementMockMvc.perform(delete("/api/typeEnseignements/{id}", typeEnseignement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TypeEnseignement> typeEnseignements = typeEnseignementRepository.findAll();
        assertThat(typeEnseignements).hasSize(databaseSizeBeforeDelete - 1);
    }
}
