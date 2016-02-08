package edu.smartschool.web.rest;

import edu.smartschool.Application;
import edu.smartschool.domain.Pays;
import edu.smartschool.repository.PaysRepository;
import edu.smartschool.repository.search.PaysSearchRepository;

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
 * Test class for the PaysResource REST controller.
 *
 * @see PaysResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PaysResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAA";
    private static final String UPDATED_INTITULE = "BBBBB";

    @Inject
    private PaysRepository paysRepository;

    @Inject
    private PaysSearchRepository paysSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPaysMockMvc;

    private Pays pays;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PaysResource paysResource = new PaysResource();
        ReflectionTestUtils.setField(paysResource, "paysSearchRepository", paysSearchRepository);
        ReflectionTestUtils.setField(paysResource, "paysRepository", paysRepository);
        this.restPaysMockMvc = MockMvcBuilders.standaloneSetup(paysResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        pays = new Pays();
        pays.setIntitule(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void createPays() throws Exception {
        int databaseSizeBeforeCreate = paysRepository.findAll().size();

        // Create the Pays

        restPaysMockMvc.perform(post("/api/payss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pays)))
                .andExpect(status().isCreated());

        // Validate the Pays in the database
        List<Pays> payss = paysRepository.findAll();
        assertThat(payss).hasSize(databaseSizeBeforeCreate + 1);
        Pays testPays = payss.get(payss.size() - 1);
        assertThat(testPays.getIntitule()).isEqualTo(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = paysRepository.findAll().size();
        // set the field null
        pays.setIntitule(null);

        // Create the Pays, which fails.

        restPaysMockMvc.perform(post("/api/payss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pays)))
                .andExpect(status().isBadRequest());

        List<Pays> payss = paysRepository.findAll();
        assertThat(payss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPayss() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        // Get all the payss
        restPaysMockMvc.perform(get("/api/payss?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pays.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())));
    }

    @Test
    @Transactional
    public void getPays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        // Get the pays
        restPaysMockMvc.perform(get("/api/payss/{id}", pays.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(pays.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPays() throws Exception {
        // Get the pays
        restPaysMockMvc.perform(get("/api/payss/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

		int databaseSizeBeforeUpdate = paysRepository.findAll().size();

        // Update the pays
        pays.setIntitule(UPDATED_INTITULE);

        restPaysMockMvc.perform(put("/api/payss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pays)))
                .andExpect(status().isOk());

        // Validate the Pays in the database
        List<Pays> payss = paysRepository.findAll();
        assertThat(payss).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = payss.get(payss.size() - 1);
        assertThat(testPays.getIntitule()).isEqualTo(UPDATED_INTITULE);
    }

    @Test
    @Transactional
    public void deletePays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

		int databaseSizeBeforeDelete = paysRepository.findAll().size();

        // Get the pays
        restPaysMockMvc.perform(delete("/api/payss/{id}", pays.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Pays> payss = paysRepository.findAll();
        assertThat(payss).hasSize(databaseSizeBeforeDelete - 1);
    }
}
