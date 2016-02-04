package edu.smartschool.web.rest;

import edu.smartschool.Application;
import edu.smartschool.domain.Cycle;
import edu.smartschool.repository.CycleRepository;
import edu.smartschool.repository.search.CycleSearchRepository;

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
 * Test class for the CycleResource REST controller.
 *
 * @see CycleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CycleResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAA";
    private static final String UPDATED_INTITULE = "BBBBB";

    @Inject
    private CycleRepository cycleRepository;

    @Inject
    private CycleSearchRepository cycleSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCycleMockMvc;

    private Cycle cycle;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CycleResource cycleResource = new CycleResource();
        ReflectionTestUtils.setField(cycleResource, "cycleSearchRepository", cycleSearchRepository);
        ReflectionTestUtils.setField(cycleResource, "cycleRepository", cycleRepository);
        this.restCycleMockMvc = MockMvcBuilders.standaloneSetup(cycleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        cycle = new Cycle();
        cycle.setIntitule(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void createCycle() throws Exception {
        int databaseSizeBeforeCreate = cycleRepository.findAll().size();

        // Create the Cycle

        restCycleMockMvc.perform(post("/api/cycles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cycle)))
                .andExpect(status().isCreated());

        // Validate the Cycle in the database
        List<Cycle> cycles = cycleRepository.findAll();
        assertThat(cycles).hasSize(databaseSizeBeforeCreate + 1);
        Cycle testCycle = cycles.get(cycles.size() - 1);
        assertThat(testCycle.getIntitule()).isEqualTo(DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = cycleRepository.findAll().size();
        // set the field null
        cycle.setIntitule(null);

        // Create the Cycle, which fails.

        restCycleMockMvc.perform(post("/api/cycles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cycle)))
                .andExpect(status().isBadRequest());

        List<Cycle> cycles = cycleRepository.findAll();
        assertThat(cycles).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCycles() throws Exception {
        // Initialize the database
        cycleRepository.saveAndFlush(cycle);

        // Get all the cycles
        restCycleMockMvc.perform(get("/api/cycles?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cycle.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())));
    }

    @Test
    @Transactional
    public void getCycle() throws Exception {
        // Initialize the database
        cycleRepository.saveAndFlush(cycle);

        // Get the cycle
        restCycleMockMvc.perform(get("/api/cycles/{id}", cycle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cycle.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCycle() throws Exception {
        // Get the cycle
        restCycleMockMvc.perform(get("/api/cycles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCycle() throws Exception {
        // Initialize the database
        cycleRepository.saveAndFlush(cycle);

		int databaseSizeBeforeUpdate = cycleRepository.findAll().size();

        // Update the cycle
        cycle.setIntitule(UPDATED_INTITULE);

        restCycleMockMvc.perform(put("/api/cycles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cycle)))
                .andExpect(status().isOk());

        // Validate the Cycle in the database
        List<Cycle> cycles = cycleRepository.findAll();
        assertThat(cycles).hasSize(databaseSizeBeforeUpdate);
        Cycle testCycle = cycles.get(cycles.size() - 1);
        assertThat(testCycle.getIntitule()).isEqualTo(UPDATED_INTITULE);
    }

    @Test
    @Transactional
    public void deleteCycle() throws Exception {
        // Initialize the database
        cycleRepository.saveAndFlush(cycle);

		int databaseSizeBeforeDelete = cycleRepository.findAll().size();

        // Get the cycle
        restCycleMockMvc.perform(delete("/api/cycles/{id}", cycle.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Cycle> cycles = cycleRepository.findAll();
        assertThat(cycles).hasSize(databaseSizeBeforeDelete - 1);
    }
}
