package com.creditflow.app.web.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.creditflow.app.TestUtil;
import com.creditflow.app.domain.enumeration.DocumentType;
import com.creditflow.app.service.dto.ClientDTO;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import liquibase.Liquibase;
import org.junit.jupiter.api.*;

@QuarkusTest
public class ClientResourceTest {

    private static final TypeRef<ClientDTO> ENTITY_TYPE = new TypeRef<>() {};

    private static final TypeRef<List<ClientDTO>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};

    private static final Long DEFAULT_CLIENT_NUMBER = 1L;
    private static final Long UPDATED_CLIENT_NUMBER = 2L;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final DocumentType DEFAULT_DOCUMENT_TYPE = DocumentType.NATIONAL_ID;
    private static final DocumentType UPDATED_DOCUMENT_TYPE = DocumentType.PASSPORT;

    private static final String DEFAULT_DOCUMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    String adminToken;

    ClientDTO clientDTO;

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
    public static ClientDTO createEntity() {
        var clientDTO = new ClientDTO();
        clientDTO.clientNumber = DEFAULT_CLIENT_NUMBER;
        clientDTO.firstName = DEFAULT_FIRST_NAME;
        clientDTO.lastName = DEFAULT_LAST_NAME;
        clientDTO.dateOfBirth = DEFAULT_DATE_OF_BIRTH;
        clientDTO.documentType = DEFAULT_DOCUMENT_TYPE;
        clientDTO.documentNumber = DEFAULT_DOCUMENT_NUMBER;
        clientDTO.email = DEFAULT_EMAIL;
        clientDTO.phone = DEFAULT_PHONE;
        return clientDTO;
    }

    @BeforeEach
    public void initTest() {
        clientDTO = createEntity();
    }

    @Test
    public void createClient() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Client
        clientDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeCreate + 1);
        var testClientDTO = clientDTOList.stream().filter(it -> clientDTO.id.equals(it.id)).findFirst().get();
        assertThat(testClientDTO.clientNumber).isEqualTo(DEFAULT_CLIENT_NUMBER);
        assertThat(testClientDTO.firstName).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testClientDTO.lastName).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testClientDTO.dateOfBirth).isEqualTo(DEFAULT_DATE_OF_BIRTH);
        assertThat(testClientDTO.documentType).isEqualTo(DEFAULT_DOCUMENT_TYPE);
        assertThat(testClientDTO.documentNumber).isEqualTo(DEFAULT_DOCUMENT_NUMBER);
        assertThat(testClientDTO.email).isEqualTo(DEFAULT_EMAIL);
        assertThat(testClientDTO.phone).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    public void createClientWithExistingId() {
        var databaseSizeBeforeCreate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Create the Client with an existing ID
        clientDTO.id = 1L;

        // An entity with an existing ID cannot be created, so this API call must fail
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkClientNumberIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        clientDTO.clientNumber = null;

        // Create the Client, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkFirstNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        clientDTO.firstName = null;

        // Create the Client, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkLastNameIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        clientDTO.lastName = null;

        // Create the Client, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkDateOfBirthIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        clientDTO.dateOfBirth = null;

        // Create the Client, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkDocumentTypeIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        clientDTO.documentType = null;

        // Create the Client, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkDocumentNumberIsRequired() throws Exception {
        var databaseSizeBeforeTest = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // set the field null
        clientDTO.documentNumber = null;

        // Create the Client, which fails.
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void updateClient() {
        // Initialize the database
        clientDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
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
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Get the client
        var updatedClientDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients/{id}", clientDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(ENTITY_TYPE);

        // Update the client
        updatedClientDTO.clientNumber = UPDATED_CLIENT_NUMBER;
        updatedClientDTO.firstName = UPDATED_FIRST_NAME;
        updatedClientDTO.lastName = UPDATED_LAST_NAME;
        updatedClientDTO.dateOfBirth = UPDATED_DATE_OF_BIRTH;
        updatedClientDTO.documentType = UPDATED_DOCUMENT_TYPE;
        updatedClientDTO.documentNumber = UPDATED_DOCUMENT_NUMBER;
        updatedClientDTO.email = UPDATED_EMAIL;
        updatedClientDTO.phone = UPDATED_PHONE;

        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(updatedClientDTO)
            .when()
            .put("/api/clients/" + clientDTO.id)
            .then()
            .statusCode(OK.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeUpdate);
        var testClientDTO = clientDTOList.stream().filter(it -> updatedClientDTO.id.equals(it.id)).findFirst().get();
        assertThat(testClientDTO.clientNumber).isEqualTo(UPDATED_CLIENT_NUMBER);
        assertThat(testClientDTO.firstName).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testClientDTO.lastName).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testClientDTO.dateOfBirth).isEqualTo(UPDATED_DATE_OF_BIRTH);
        assertThat(testClientDTO.documentType).isEqualTo(UPDATED_DOCUMENT_TYPE);
        assertThat(testClientDTO.documentNumber).isEqualTo(UPDATED_DOCUMENT_NUMBER);
        assertThat(testClientDTO.email).isEqualTo(UPDATED_EMAIL);
        assertThat(testClientDTO.phone).isEqualTo(UPDATED_PHONE);
    }

    @Test
    public void updateNonExistingClient() {
        var databaseSizeBeforeUpdate = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
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
            .body(clientDTO)
            .when()
            .put("/api/clients/" + Long.MAX_VALUE)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());

        // Validate the Client in the database
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteClient() {
        // Initialize the database
        clientDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
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
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE)
            .size();

        // Delete the client
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .delete("/api/clients/{id}", clientDTO.id)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        // Validate the database contains one less item
        var clientDTOList = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .extract()
            .as(LIST_OF_ENTITY_TYPE);

        assertThat(clientDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllClients() {
        // Initialize the database
        clientDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        // Get all the clientList
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients?sort=id,desc")
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", hasItem(clientDTO.id.intValue()))
            .body("clientNumber", hasItem(DEFAULT_CLIENT_NUMBER.intValue()))
            .body("firstName", hasItem(DEFAULT_FIRST_NAME))
            .body("lastName", hasItem(DEFAULT_LAST_NAME))
            .body("dateOfBirth", hasItem(TestUtil.formatDateTime(DEFAULT_DATE_OF_BIRTH)))
            .body("documentType", hasItem(DEFAULT_DOCUMENT_TYPE.toString()))
            .body("documentNumber", hasItem(DEFAULT_DOCUMENT_NUMBER))
            .body("email", hasItem(DEFAULT_EMAIL))
            .body("phone", hasItem(DEFAULT_PHONE));
    }

    @Test
    public void getClient() {
        // Initialize the database
        clientDTO = given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(clientDTO)
            .when()
            .post("/api/clients")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract()
            .as(ENTITY_TYPE);

        var response = // Get the client
            given()
                .auth()
                .preemptive()
                .oauth2(adminToken)
                .accept(APPLICATION_JSON)
                .when()
                .get("/api/clients/{id}", clientDTO.id)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ENTITY_TYPE);

        // Get the client
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients/{id}", clientDTO.id)
            .then()
            .statusCode(OK.getStatusCode())
            .contentType(APPLICATION_JSON)
            .body("id", is(clientDTO.id.intValue()))
            .body("clientNumber", is(DEFAULT_CLIENT_NUMBER.intValue()))
            .body("firstName", is(DEFAULT_FIRST_NAME))
            .body("lastName", is(DEFAULT_LAST_NAME))
            .body("dateOfBirth", is(TestUtil.formatDateTime(DEFAULT_DATE_OF_BIRTH)))
            .body("documentType", is(DEFAULT_DOCUMENT_TYPE.toString()))
            .body("documentNumber", is(DEFAULT_DOCUMENT_NUMBER))
            .body("email", is(DEFAULT_EMAIL))
            .body("phone", is(DEFAULT_PHONE));
    }

    @Test
    public void getNonExistingClient() {
        // Get the client
        given()
            .auth()
            .preemptive()
            .oauth2(adminToken)
            .accept(APPLICATION_JSON)
            .when()
            .get("/api/clients/{id}", Long.MAX_VALUE)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
