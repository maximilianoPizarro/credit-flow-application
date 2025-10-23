package com.creditflow.app.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.creditflow.app.TestUtil;
import com.creditflow.app.domain.enumeration.MovementType;
import com.creditflow.app.service.dto.CreditMovementDTO;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import liquibase.Liquibase;
import org.junit.jupiter.api.*;

@QuarkusTest
public class CreditMovementResourceTest {

    private static final TypeRef<CreditMovementDTO> ENTITY_TYPE = new TypeRef<>() {};

    private static final TypeRef<List<CreditMovementDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};

    private static final ZonedDateTime DEFAULT_MOVEMENT_DATE = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(0L).truncatedTo(ChronoUnit.SECONDS),
        ZoneOffset.UTC
    );
    private static final ZonedDateTime UPDATED_MOVEMENT_DATE = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);

    private static final MovementType DEFAULT_TYPE = MovementType.PAYMENT;
    private static final MovementType UPDATED_TYPE = MovementType.CHARGE;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_REFERENCE = "BBBBBBBBBB";

    String adminToken;

    CreditMovementDTO creditMovementDTO;

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
    public static CreditMovementDTO createEntity() {
        var creditMovementDTO = new CreditMovementDTO();
        creditMovementDTO.movementDate = DEFAULT_MOVEMENT_DATE;
        creditMovementDTO.type = DEFAULT_TYPE;
        creditMovementDTO.amount = DEFAULT_AMOUNT;
        creditMovementDTO.description = DEFAULT_DESCRIPTION;
        creditMovementDTO.externalReference = DEFAULT_EXTERNAL_REFERENCE;
        return creditMovementDTO;
    }

    @BeforeEach
    public void initTest() {
        creditMovementDTO = createEntity();
    }

    @Test
    public void createCreditMovement() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CreditMovement
        creditMovementDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testCreditMovementDTO = creditMovementDTOList.stream().filter(it -> creditMovementDTO.id.equals(it.id)).findFirst().get();
        assertThat(testCreditMovementDTO.movementDate).isEqualTo(DEFAULT_MOVEMENT_DATE);
        assertThat(testCreditMovementDTO.type).isEqualTo(DEFAULT_TYPE);
        assertThat(testCreditMovementDTO.amount).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testCreditMovementDTO.description).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCreditMovementDTO.externalReference).isEqualTo(DEFAULT_EXTERNAL_REFERENCE);
    }

    @Test
    public void createCreditMovementWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the CreditMovement with an existing ID
        creditMovementDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkMovementDateIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        creditMovementDTO.movementDate = null;

        // Create the CreditMovement, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkTypeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        creditMovementDTO.type = null;

        // Create the CreditMovement, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkAmountIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        creditMovementDTO.amount = null;

        // Create the CreditMovement, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateCreditMovement() {
        // Initialize the database
        creditMovementDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
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
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the creditMovement
        var updatedCreditMovementDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements/{id}", creditMovementDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(ENTITY_TYPE);

        // Update the creditMovement
        updatedCreditMovementDTO.movementDate = UPDATED_MOVEMENT_DATE;
        updatedCreditMovementDTO.type = UPDATED_TYPE;
        updatedCreditMovementDTO.amount = UPDATED_AMOUNT;
        updatedCreditMovementDTO.description = UPDATED_DESCRIPTION;
        updatedCreditMovementDTO.externalReference = UPDATED_EXTERNAL_REFERENCE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedCreditMovementDTO)
            .when()
            .put("/api/credit-movements/" + creditMovementDTO.id)
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeUpdate);
        var testCreditMovementDTO = creditMovementDTOList
            .stream()
            .filter(it -> updatedCreditMovementDTO.id.equals(it.id))
            .findFirst()
            .get();
        assertThat(testCreditMovementDTO.movementDate).isEqualTo(UPDATED_MOVEMENT_DATE);
        assertThat(testCreditMovementDTO.type).isEqualTo(UPDATED_TYPE);
        assertThat(testCreditMovementDTO.amount).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testCreditMovementDTO.description).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCreditMovementDTO.externalReference).isEqualTo(UPDATED_EXTERNAL_REFERENCE);
    }

    @Test
    public void updateNonExistingCreditMovement() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
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
            .body(creditMovementDTO)
            .when()
            .put("/api/credit-movements/" + Long.MAX_VALUE)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the CreditMovement in the database
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCreditMovement() {
        // Initialize the database
        creditMovementDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
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
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the creditMovement
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/credit-movements/{id}", creditMovementDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var creditMovementDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(creditMovementDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllCreditMovements() {
        // Initialize the database
        creditMovementDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Get all the creditMovementList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(creditMovementDTO.id.intValue()))
            .body("movementDate", hasItem(TestUtil.formatDateTime(DEFAULT_MOVEMENT_DATE)))
            .body("type", hasItem(DEFAULT_TYPE.toString()))
            .body("amount", hasItem(DEFAULT_AMOUNT.floatValue()))
            .body("description", hasItem(DEFAULT_DESCRIPTION))
            .body("externalReference", hasItem(DEFAULT_EXTERNAL_REFERENCE));
    }

    @Test
    public void getCreditMovement() {
        // Initialize the database
        creditMovementDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(creditMovementDTO)
            .when()
            .post("/api/credit-movements")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        var response = // Get the creditMovement
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/credit-movements/{id}", creditMovementDTO.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ENTITY_TYPE);

        // Get the creditMovement
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements/{id}", creditMovementDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(creditMovementDTO.id.intValue()))
            .body("movementDate", is(TestUtil.formatDateTime(DEFAULT_MOVEMENT_DATE)))
            .body("type", is(DEFAULT_TYPE.toString()))
            .body("amount", comparesEqualTo(DEFAULT_AMOUNT.floatValue()))
            .body("description", is(DEFAULT_DESCRIPTION))
            .body("externalReference", is(DEFAULT_EXTERNAL_REFERENCE));
    }

    @Test
    public void getNonExistingCreditMovement() {
        // Get the creditMovement
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/credit-movements/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
