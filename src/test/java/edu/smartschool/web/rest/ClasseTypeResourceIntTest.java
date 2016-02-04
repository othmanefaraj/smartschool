package edu.smartschool.web.rest;

import edu.smartschool.Application;
import edu.smartschool.domain.ClasseType;
import edu.smartschool.repository.ClasseTypeRepository;
import edu.smartschool.repository.search.ClasseTypeSearchRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ClasseTypeResource REST controller.
 *
 * @see ClasseTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ClasseTypeResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAA";
    private static final String UPDATED_INTITULE = "BBBBB";

    private static final LocalDate DEFAULT_DATE_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_CREATION = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private ClasseTypeRepository classeTypeRepository;

    @Inject
    private ClasseTypeSearchRepository classeTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restClasseTypeMockMvc;

    private ClasseType classeType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClasseTypeResource classeTypeResource = new ClasseTypeResource();
        ReflectionTestUtils.setField(classeTypeResource, "classeTypeSearchRepository", classeTypeSearchRepository);
        ReflectionTestUtils.setField(classeTypeResource, "classeTypeRepository", classeTypeRepository);
        this.restClasseTypeMockMvc = MockMvcBuilders.standaloneSetup(classeTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        classeType = new ClasseType();
        classeType.setIntitule(DEFAULT_INTITULE);
        classeType.setDateCreation(DEFAULT_DATE_CREATION);
    }

    @Test
    @Transactional
    public void createClasseType() throws Exception {
        int databaseSizeBeforeCreate = classeTypeRepository.findAll().size();

        // Create the ClasseType

        restClasseTypeMockMvc.perform(post("/api/classeTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(classeType)))
                .andExpect(status().isCreated());

        // Validate the ClasseType in the database
        List<ClasseType> classeTypes = classeTypeRepository.findAll();
        assertThat(classeTypes).hasSize(databaseSizeBeforeCreate + 1);
        ClasseType testClasseType = classeTypes.get(classeTypes.size() - 1);
        assertThat(testClasseType.getIntitule()).isEqualTo(DEFAULT_INTITULE);
        assertThat(testClasseType.getDateCreation()).isEqualTo(DEFAULT_DATE_CREATION);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = classeTypeRepository.findAll().size();
        // set the field null
        classeType.setIntitule(null);

        // Create the ClasseType, which fails.

        restClasseTypeMockMvc.perform(post("/api/classeTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(classeType)))
                .andExpect(status().isBadRequest());

        List<ClasseType> classeTypes = classeTypeRepository.findAll();
        assertThat(classeTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClasseTypes() throws Exception {
        // Initialize the database
        classeTypeRepository.saveAndFlush(classeType);

        // Get all the classeTypes
        restClasseTypeMockMvc.perform(get("/api/classeTypes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(classeType.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())))
                .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }

    @Test
    @Transactional
    public void getClasseType() throws Exception {
        // Initialize the database
        classeTypeRepository.saveAndFlush(classeType);

        // Get the classeType
        restClasseTypeMockMvc.perform(get("/api/classeTypes/{id}", classeType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(classeType.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClasseType() throws Exception {
        // Get the classeType
        restClasseTypeMockMvc.perform(get("/api/classeTypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClasseType() throws Exception {
        // Initialize the database
        classeTypeRepository.saveAndFlush(classeType);

		int databaseSizeBeforeUpdate = classeTypeRepository.findAll().size();

        // Update the classeType
        classeType.setIntitule(UPDATED_INTITULE);
        classeType.setDateCreation(UPDATED_DATE_CREATION);

        restClasseTypeMockMvc.perform(put("/api/classeTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(classeType)))
                .andExpect(status().isOk());

        // Validate the ClasseType in the database
        List<ClasseType> classeTypes = classeTypeRepository.findAll();
        assertThat(classeTypes).hasSize(databaseSizeBeforeUpdate);
        ClasseType testClasseType = classeTypes.get(classeTypes.size() - 1);
        assertThat(testClasseType.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testClasseType.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void deleteClasseType() throws Exception {
        // Initialize the database
        classeTypeRepository.saveAndFlush(classeType);

		int databaseSizeBeforeDelete = classeTypeRepository.findAll().size();

        // Get the classeType
        restClasseTypeMockMvc.perform(delete("/api/classeTypes/{id}", classeType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ClasseType> classeTypes = classeTypeRepository.findAll();
        assertThat(classeTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
