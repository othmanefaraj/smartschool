package edu.smartschool.web.rest;

import edu.smartschool.Application;
import edu.smartschool.domain.Optionn;
import edu.smartschool.repository.OptionnRepository;
import edu.smartschool.repository.search.OptionnSearchRepository;

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
 * Test class for the OptionnResource REST controller.
 *
 * @see OptionnResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class OptionnResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAA";
    private static final String UPDATED_INTITULE = "BBBBB";

    @Inject
    private OptionnRepository optionnRepository;

    @Inject
    private OptionnSearchRepository optionnSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOptionnMockMvc;

    private Optionn optionn;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OptionnResource optionnResource = new OptionnResource();
        ReflectionTestUtils.setField(optionnResource, "optionnSearchRepository", optionnSearchRepository);
        ReflectionTestUtils.setField(optionnResource, "optionnRepository", optionnRepository);
        this.restOptionnMockMvc = MockMvcBuilders.standaloneSetup(optionnResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        optionn = new Optionn();
        optionn.setIntitule(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void createOptionn() throws Exception {
        int databaseSizeBeforeCreate = optionnRepository.findAll().size();

        // Create the Optionn

        restOptionnMockMvc.perform(post("/api/optionns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionn)))
                .andExpect(status().isCreated());

        // Validate the Optionn in the database
        List<Optionn> optionns = optionnRepository.findAll();
        assertThat(optionns).hasSize(databaseSizeBeforeCreate + 1);
        Optionn testOptionn = optionns.get(optionns.size() - 1);
        assertThat(testOptionn.getIntitule()).isEqualTo(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = optionnRepository.findAll().size();
        // set the field null
        optionn.setIntitule(null);

        // Create the Optionn, which fails.

        restOptionnMockMvc.perform(post("/api/optionns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionn)))
                .andExpect(status().isBadRequest());

        List<Optionn> optionns = optionnRepository.findAll();
        assertThat(optionns).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOptionns() throws Exception {
        // Initialize the database
        optionnRepository.saveAndFlush(optionn);

        // Get all the optionns
        restOptionnMockMvc.perform(get("/api/optionns?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(optionn.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())));
    }

    @Test
    @Transactional
    public void getOptionn() throws Exception {
        // Initialize the database
        optionnRepository.saveAndFlush(optionn);

        // Get the optionn
        restOptionnMockMvc.perform(get("/api/optionns/{id}", optionn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(optionn.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOptionn() throws Exception {
        // Get the optionn
        restOptionnMockMvc.perform(get("/api/optionns/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOptionn() throws Exception {
        // Initialize the database
        optionnRepository.saveAndFlush(optionn);

		int databaseSizeBeforeUpdate = optionnRepository.findAll().size();

        // Update the optionn
        optionn.setIntitule(UPDATED_INTITULE);

        restOptionnMockMvc.perform(put("/api/optionns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionn)))
                .andExpect(status().isOk());

        // Validate the Optionn in the database
        List<Optionn> optionns = optionnRepository.findAll();
        assertThat(optionns).hasSize(databaseSizeBeforeUpdate);
        Optionn testOptionn = optionns.get(optionns.size() - 1);
        assertThat(testOptionn.getIntitule()).isEqualTo(UPDATED_INTITULE);
    }

    @Test
    @Transactional
    public void deleteOptionn() throws Exception {
        // Initialize the database
        optionnRepository.saveAndFlush(optionn);

		int databaseSizeBeforeDelete = optionnRepository.findAll().size();

        // Get the optionn
        restOptionnMockMvc.perform(delete("/api/optionns/{id}", optionn.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Optionn> optionns = optionnRepository.findAll();
        assertThat(optionns).hasSize(databaseSizeBeforeDelete - 1);
    }
}
