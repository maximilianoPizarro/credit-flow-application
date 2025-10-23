package com.creditflow.app.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.creditflow.app.TestUtil;
import com.creditflow.app.domain.enumeration.PlanType;
import com.creditflow.app.service.dto.CreditPlanDTO;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import liquibase.Liquibase;
import org.junit.jupiter.api.*;

@QuarkusTest
public class CreditPlanResourceTest {

    private static final TypeRef<CreditPlanDTO> ENTITY_TYPE = new TypeRef<>() {};

    private static final TypeRef<List<CreditPlanDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final PlanType DEFAULT_TYPE = PlanType.MORTGAGE;
    private static final PlanType UPDATED_TYPE = PlanType.PERSONAL;

    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_INTEREST_RATE = new BigDecimal(2);

    private static final Integer DEFAULT_MAX_TERM_YEARS = 1;
    private static final Integer UPDATED_MAX_TERM_YEARS = 2;

    String adminToken;

    CreditPlanDTO creditPlanDTO;

    @Inject
    LiquibaseFactory liquibaseFactory;

    @BeforeAll
    static void jsonMapper() {
        RestAssured.config = RestAssured.config()
            .objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
    }

    @BeforeEach
    public void authenticateAdmin() {
        this.adminToken = TestUtil.getAdminToken();
    }

    @BeforeEach
    public void databaseFixture() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            liquibase.dropAll();
            liquibase.validate();
            liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CreditPlanDTO createEntity() {
        var creditPlanDTO = new CreditPlanDTO();
        creditPlanDTO.name = DEFAULT_NAME;
        creditPlanDTO.description = DEFAULT_DESCRIPTION;
        creditPlanDTO.type = DEFAULT_TYPE;
        creditPlanDTO.interestRate = DEFAULT_INTEREST_RATE;
        creditPlanDTO.maxTermYears = DEFAULT_MAX_TERM_YEARS;
        return creditPlanDTO;
    }

    @BeforeEach
    public void initTest() {
        creditPlanDTO = createEntity();
    }

    @Test
    public void createCreditPlan() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CreditPlan
        creditPlanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testCreditPlanDTO = creditPlanDTOList.stream().filter(it -> creditPlanDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCreditPlanDTO.name).isEqualTo(DEFAULT_NAME);
        assertThat(testCreditPlanDTO.description).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCreditPlanDTO.type).isEqualTo(DEFAULT_TYPE);
        assertThat(testCreditPlanDTO.interestRate).isEqualByComparingTo(DEFAULT_INTEREST_RATE);
        assertThat(testCreditPlanDTO.maxTermYears).isEqualTo(DEFAULT_MAX_TERM_YEARS);
    }

    @Test
    public void createCreditPlanWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CreditPlan with an existing ID
        creditPlanDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        creditPlanDTO.name = null;

        // Create the CreditPlan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkTypeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        creditPlanDTO.type = null;

        // Create the CreditPlan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkInterestRateIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        creditPlanDTO.interestRate = null;

        // Create the CreditPlan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCreditPlan() {
        // Initialize the database
        creditPlanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the creditPlan
        var updatedCreditPlanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans/{id}", creditPlanDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(ENTITY_TYPE);

        // Update the creditPlan
        updatedCreditPlanDTO.name = UPDATED_NAME;
        updatedCreditPlanDTO.description = UPDATED_DESCRIPTION;
        updatedCreditPlanDTO.type = UPDATED_TYPE;
        updatedCreditPlanDTO.interestRate = UPDATED_INTEREST_RATE;
        updatedCreditPlanDTO.maxTermYears = UPDATED_MAX_TERM_YEARS;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCreditPlanDTO)
            .when()
            .put("/api/credit-plans/" + creditPlanDTO.id)
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeUpdate);
        var testCreditPlanDTO = creditPlanDTOList.stream().filter(it -> updatedCreditPlanDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCreditPlanDTO.name).isEqualTo(UPDATED_NAME);
        assertThat(testCreditPlanDTO.description).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCreditPlanDTO.type).isEqualTo(UPDATED_TYPE);
        assertThat(testCreditPlanDTO.interestRate).isEqualByComparingTo(UPDATED_INTEREST_RATE);
        assertThat(testCreditPlanDTO.maxTermYears).isEqualTo(UPDATED_MAX_TERM_YEARS);
    }

    @Test
    public void updateNonExistingCreditPlan() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .put("/api/credit-plans/" + Long.MAX_VALUE)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditPlan in the database
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCreditPlan() {
        // Initialize the database
        creditPlanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        var databaseSizeBeforeDelete = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the creditPlan
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/credit-plans/{id}", creditPlanDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var creditPlanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditPlanDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCreditPlans() {
        // Initialize the database
        creditPlanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Get all the creditPlanList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(creditPlanDTO.id.intValue()))
            .body("name", hasItem(DEFAULT_NAME))
            .body("description", hasItem(DEFAULT_DESCRIPTION.toString()))
            .body("type", hasItem(DEFAULT_TYPE.toString()))
            .body("interestRate", hasItem(DEFAULT_INTEREST_RATE.floatValue()))
            .body("maxTermYears", hasItem(DEFAULT_MAX_TERM_YEARS.intValue()));
    }

    @Test
    public void getCreditPlan() {
        // Initialize the database
        creditPlanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditPlanDTO)
            .when()
            .post("/api/credit-plans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        var response = // Get the creditPlan
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/credit-plans/{id}", creditPlanDTO.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ENTITY_TYPE);

        // Get the creditPlan
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans/{id}", creditPlanDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(creditPlanDTO.id.intValue()))
            .body("name", is(DEFAULT_NAME))
            .body("description", is(DEFAULT_DESCRIPTION.toString()))
            .body("type", is(DEFAULT_TYPE.toString()))
            .body("interestRate", comparesEqualTo(DEFAULT_INTEREST_RATE.floatValue()))
            .body("maxTermYears", is(DEFAULT_MAX_TERM_YEARS.intValue()));
    }

    @Test
    public void getNonExistingCreditPlan() {
        // Get the creditPlan
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-plans/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
