package com.creditflow.app.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.creditflow.app.TestUtil;
import com.creditflow.app.domain.enumeration.LoanStatus;
import com.creditflow.app.service.dto.LoanDTO;
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
public class LoanResourceTest {

    private static final TypeRef<LoanDTO> ENTITY_TYPE = new TypeRef<>() {};

    private static final TypeRef<List<LoanDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};

    private static final String DEFAULT_LOAN_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_LOAN_NUMBER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_OPENING_DATE = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(0L).truncatedTo(ChronoUnit.SECONDS),
        ZoneOffset.UTC
    );
    private static final ZonedDateTime UPDATED_OPENING_DATE = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);

    private static final BigDecimal DEFAULT_PRINCIPAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRINCIPAL_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_OUTSTANDING_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OUTSTANDING_BALANCE = new BigDecimal(2);

    private static final LoanStatus DEFAULT_STATUS = LoanStatus.ACTIVE;
    private static final LoanStatus UPDATED_STATUS = LoanStatus.OVERDUE;

    private static final ZonedDateTime DEFAULT_CLOSING_DATE = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(0L).truncatedTo(ChronoUnit.SECONDS),
        ZoneOffset.UTC
    );
    private static final ZonedDateTime UPDATED_CLOSING_DATE = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);

    String adminToken;

    LoanDTO loanDTO;

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
    public static LoanDTO createEntity() {
        var loanDTO = new LoanDTO();
        loanDTO.loanNumber = DEFAULT_LOAN_NUMBER;
        loanDTO.openingDate = DEFAULT_OPENING_DATE;
        loanDTO.principalAmount = DEFAULT_PRINCIPAL_AMOUNT;
        loanDTO.outstandingBalance = DEFAULT_OUTSTANDING_BALANCE;
        loanDTO.status = DEFAULT_STATUS;
        loanDTO.closingDate = DEFAULT_CLOSING_DATE;
        return loanDTO;
    }

    @BeforeEach
    public void initTest() {
        loanDTO = createEntity();
    }

    @Test
    public void createLoan() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Loan
        loanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testLoanDTO = loanDTOList.stream().filter(it -> loanDTO.id.equals(it.id)).findFirst().get();
        assertThat(testLoanDTO.loanNumber).isEqualTo(DEFAULT_LOAN_NUMBER);
        assertThat(testLoanDTO.openingDate).isEqualTo(DEFAULT_OPENING_DATE);
        assertThat(testLoanDTO.principalAmount).isEqualByComparingTo(DEFAULT_PRINCIPAL_AMOUNT);
        assertThat(testLoanDTO.outstandingBalance).isEqualByComparingTo(DEFAULT_OUTSTANDING_BALANCE);
        assertThat(testLoanDTO.status).isEqualTo(DEFAULT_STATUS);
        assertThat(testLoanDTO.closingDate).isEqualTo(DEFAULT_CLOSING_DATE);
    }

    @Test
    public void createLoanWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Loan with an existing ID
        loanDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkLoanNumberIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        loanDTO.loanNumber = null;

        // Create the Loan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkOpeningDateIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        loanDTO.openingDate = null;

        // Create the Loan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkPrincipalAmountIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        loanDTO.principalAmount = null;

        // Create the Loan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkStatusIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        loanDTO.status = null;

        // Create the Loan, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateLoan() {
        // Initialize the database
        loanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
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
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the loan
        var updatedLoanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans/{id}", loanDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(ENTITY_TYPE);

        // Update the loan
        updatedLoanDTO.loanNumber = UPDATED_LOAN_NUMBER;
        updatedLoanDTO.openingDate = UPDATED_OPENING_DATE;
        updatedLoanDTO.principalAmount = UPDATED_PRINCIPAL_AMOUNT;
        updatedLoanDTO.outstandingBalance = UPDATED_OUTSTANDING_BALANCE;
        updatedLoanDTO.status = UPDATED_STATUS;
        updatedLoanDTO.closingDate = UPDATED_CLOSING_DATE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedLoanDTO)
            .when()
            .put("/api/loans/" + loanDTO.id)
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeUpdate);
        var testLoanDTO = loanDTOList.stream().filter(it -> updatedLoanDTO.id.equals(it.id)).findFirst().get();
        assertThat(testLoanDTO.loanNumber).isEqualTo(UPDATED_LOAN_NUMBER);
        assertThat(testLoanDTO.openingDate).isEqualTo(UPDATED_OPENING_DATE);
        assertThat(testLoanDTO.principalAmount).isEqualByComparingTo(UPDATED_PRINCIPAL_AMOUNT);
        assertThat(testLoanDTO.outstandingBalance).isEqualByComparingTo(UPDATED_OUTSTANDING_BALANCE);
        assertThat(testLoanDTO.status).isEqualTo(UPDATED_STATUS);
        assertThat(testLoanDTO.closingDate).isEqualTo(UPDATED_CLOSING_DATE);
    }

    @Test
    public void updateNonExistingLoan() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
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
            .body(loanDTO)
            .when()
            .put("/api/loans/" + Long.MAX_VALUE)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Loan in the database
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteLoan() {
        // Initialize the database
        loanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
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
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the loan
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/loans/{id}", loanDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var loanDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(loanDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllLoans() {
        // Initialize the database
        loanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Get all the loanList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(loanDTO.id.intValue()))
            .body("loanNumber", hasItem(DEFAULT_LOAN_NUMBER))
            .body("openingDate", hasItem(TestUtil.formatDateTime(DEFAULT_OPENING_DATE)))
            .body("principalAmount", hasItem(DEFAULT_PRINCIPAL_AMOUNT.floatValue()))
            .body("outstandingBalance", hasItem(DEFAULT_OUTSTANDING_BALANCE.floatValue()))
            .body("status", hasItem(DEFAULT_STATUS.toString()))
            .body("closingDate", hasItem(TestUtil.formatDateTime(DEFAULT_CLOSING_DATE)));
    }

    @Test
    public void getLoan() {
        // Initialize the database
        loanDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(loanDTO)
            .when()
            .post("/api/loans")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        var response = // Get the loan
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/loans/{id}", loanDTO.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ENTITY_TYPE);

        // Get the loan
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans/{id}", loanDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(loanDTO.id.intValue()))
            .body("loanNumber", is(DEFAULT_LOAN_NUMBER))
            .body("openingDate", is(TestUtil.formatDateTime(DEFAULT_OPENING_DATE)))
            .body("principalAmount", comparesEqualTo(DEFAULT_PRINCIPAL_AMOUNT.floatValue()))
            .body("outstandingBalance", comparesEqualTo(DEFAULT_OUTSTANDING_BALANCE.floatValue()))
            .body("status", is(DEFAULT_STATUS.toString()))
            .body("closingDate", is(TestUtil.formatDateTime(DEFAULT_CLOSING_DATE)));
    }

    @Test
    public void getNonExistingLoan() {
        // Get the loan
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/loans/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
