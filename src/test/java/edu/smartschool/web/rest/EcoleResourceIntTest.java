package edu.smartschool.web.rest;

import edu.smartschool.Application;
import edu.smartschool.domain.Ecole;
import edu.smartschool.repository.EcoleRepository;
import edu.smartschool.repository.search.EcoleSearchRepository;

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

import edu.smartschool.domain.enumeration.Categorie;

/**
 * Test class for the EcoleResource REST controller.
 *
 * @see EcoleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class EcoleResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAA";
    private static final String UPDATED_INTITULE = "BBBBB";
    private static final String DEFAULT_ADRESSE = "AAAAA";
    private static final String UPDATED_ADRESSE = "BBBBB";
    private static final String DEFAULT_SITE_WEB = "AAAAA";
    private static final String UPDATED_SITE_WEB = "BBBBB";
    
    private static final Categorie DEFAULT_CATEGORIE = Categorie.Public;
    private static final Categorie UPDATED_CATEGORIE = Categorie.Privee;
    private static final String DEFAULT_GPS_LATITUDE = "AAAAA";
    private static final String UPDATED_GPS_LATITUDE = "BBBBB";
    private static final String DEFAULT_GPS_LONGITUDE = "AAAAA";
    private static final String UPDATED_GPS_LONGITUDE = "BBBBB";

    @Inject
    private EcoleRepository ecoleRepository;

    @Inject
    private EcoleSearchRepository ecoleSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEcoleMockMvc;

    private Ecole ecole;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EcoleResource ecoleResource = new EcoleResource();
        ReflectionTestUtils.setField(ecoleResource, "ecoleSearchRepository", ecoleSearchRepository);
        ReflectionTestUtils.setField(ecoleResource, "ecoleRepository", ecoleRepository);
        this.restEcoleMockMvc = MockMvcBuilders.standaloneSetup(ecoleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        ecole = new Ecole();
        ecole.setIntitule(DEFAULT_INTITULE);
        ecole.setAdresse(DEFAULT_ADRESSE);
        ecole.setSiteWeb(DEFAULT_SITE_WEB);
        ecole.setCategorie(DEFAULT_CATEGORIE);
        ecole.setGpsLatitude(DEFAULT_GPS_LATITUDE);
        ecole.setGpsLongitude(DEFAULT_GPS_LONGITUDE);
    }

    @Test
    @Transactional
    public void createEcole() throws Exception {
        int databaseSizeBeforeCreate = ecoleRepository.findAll().size();

        // Create the Ecole

        restEcoleMockMvc.perform(post("/api/ecoles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ecole)))
                .andExpect(status().isCreated());

        // Validate the Ecole in the database
        List<Ecole> ecoles = ecoleRepository.findAll();
        assertThat(ecoles).hasSize(databaseSizeBeforeCreate + 1);
        Ecole testEcole = ecoles.get(ecoles.size() - 1);
        assertThat(testEcole.getIntitule()).isEqualTo(DEFAULT_INTITULE);
        assertThat(testEcole.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testEcole.getSiteWeb()).isEqualTo(DEFAULT_SITE_WEB);
        assertThat(testEcole.getCategorie()).isEqualTo(DEFAULT_CATEGORIE);
        assertThat(testEcole.getGpsLatitude()).isEqualTo(DEFAULT_GPS_LATITUDE);
        assertThat(testEcole.getGpsLongitude()).isEqualTo(DEFAULT_GPS_LONGITUDE);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = ecoleRepository.findAll().size();
        // set the field null
        ecole.setIntitule(null);

        // Create the Ecole, which fails.

        restEcoleMockMvc.perform(post("/api/ecoles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ecole)))
                .andExpect(status().isBadRequest());

        List<Ecole> ecoles = ecoleRepository.findAll();
        assertThat(ecoles).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEcoles() throws Exception {
        // Initialize the database
        ecoleRepository.saveAndFlush(ecole);

        // Get all the ecoles
        restEcoleMockMvc.perform(get("/api/ecoles?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ecole.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())))
                .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE.toString())))
                .andExpect(jsonPath("$.[*].siteWeb").value(hasItem(DEFAULT_SITE_WEB.toString())))
                .andExpect(jsonPath("$.[*].categorie").value(hasItem(DEFAULT_CATEGORIE.toString())))
                .andExpect(jsonPath("$.[*].gpsLatitude").value(hasItem(DEFAULT_GPS_LATITUDE.toString())))
                .andExpect(jsonPath("$.[*].gpsLongitude").value(hasItem(DEFAULT_GPS_LONGITUDE.toString())));
    }

    @Test
    @Transactional
    public void getEcole() throws Exception {
        // Initialize the database
        ecoleRepository.saveAndFlush(ecole);

        // Get the ecole
        restEcoleMockMvc.perform(get("/api/ecoles/{id}", ecole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ecole.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE.toString()))
            .andExpect(jsonPath("$.siteWeb").value(DEFAULT_SITE_WEB.toString()))
            .andExpect(jsonPath("$.categorie").value(DEFAULT_CATEGORIE.toString()))
            .andExpect(jsonPath("$.gpsLatitude").value(DEFAULT_GPS_LATITUDE.toString()))
            .andExpect(jsonPath("$.gpsLongitude").value(DEFAULT_GPS_LONGITUDE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEcole() throws Exception {
        // Get the ecole
        restEcoleMockMvc.perform(get("/api/ecoles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEcole() throws Exception {
        // Initialize the database
        ecoleRepository.saveAndFlush(ecole);

		int databaseSizeBeforeUpdate = ecoleRepository.findAll().size();

        // Update the ecole
        ecole.setIntitule(UPDATED_INTITULE);
        ecole.setAdresse(UPDATED_ADRESSE);
        ecole.setSiteWeb(UPDATED_SITE_WEB);
        ecole.setCategorie(UPDATED_CATEGORIE);
        ecole.setGpsLatitude(UPDATED_GPS_LATITUDE);
        ecole.setGpsLongitude(UPDATED_GPS_LONGITUDE);

        restEcoleMockMvc.perform(put("/api/ecoles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ecole)))
                .andExpect(status().isOk());

        // Validate the Ecole in the database
        List<Ecole> ecoles = ecoleRepository.findAll();
        assertThat(ecoles).hasSize(databaseSizeBeforeUpdate);
        Ecole testEcole = ecoles.get(ecoles.size() - 1);
        assertThat(testEcole.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testEcole.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testEcole.getSiteWeb()).isEqualTo(UPDATED_SITE_WEB);
        assertThat(testEcole.getCategorie()).isEqualTo(UPDATED_CATEGORIE);
        assertThat(testEcole.getGpsLatitude()).isEqualTo(UPDATED_GPS_LATITUDE);
        assertThat(testEcole.getGpsLongitude()).isEqualTo(UPDATED_GPS_LONGITUDE);
    }

    @Test
    @Transactional
    public void deleteEcole() throws Exception {
        // Initialize the database
        ecoleRepository.saveAndFlush(ecole);

		int databaseSizeBeforeDelete = ecoleRepository.findAll().size();

        // Get the ecole
        restEcoleMockMvc.perform(delete("/api/ecoles/{id}", ecole.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ecole> ecoles = ecoleRepository.findAll();
        assertThat(ecoles).hasSize(databaseSizeBeforeDelete - 1);
    }
}
