package com.creditflow.app.web.rest;

import static jakarta.ws.rs.core.UriBuilder.fromPath;

import com.creditflow.app.service.ClientService;
import com.creditflow.app.service.dto.ClientDTO;
import com.creditflow.app.web.rest.errors.BadRequestAlertException;
import com.creditflow.app.web.util.HeaderUtil;
import com.creditflow.app.web.util.ResponseUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing {@link com.creditflow.app.domain.Client}.
 */
@Path("/api/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ClientResource {

    private final Logger log = LoggerFactory.getLogger(ClientResource.class);

    private static final String ENTITY_NAME = "client";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    ClientService clientService;

    /**
     * {@code POST  /clients} : Create a new client.
     *
     * @param clientDTO the clientDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new clientDTO, or with status {@code 400 (Bad Request)} if the client has already an ID.
     */
    @POST
    public Response createClient(@Valid ClientDTO clientDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save Client : {}", clientDTO);
        if (clientDTO.id != null) {
            throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = clientService.persistOrUpdate(clientDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /clients} : Updates an existing client.
     *
     * @param clientDTO the clientDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated clientDTO,
     * or with status {@code 400 (Bad Request)} if the clientDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientDTO couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    public Response updateClient(@Valid ClientDTO clientDTO, @PathParam("id") Long id) {
        log.debug("REST request to update Client : {}", clientDTO);
        if (clientDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = clientService.persistOrUpdate(clientDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /clients/:id} : delete the "id" client.
     *
     * @param id the id of the clientDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        log.debug("REST request to delete Client : {}", id);
        clientService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /clients} : get all the clients.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of clients in body.
     */
    @GET
    public List<ClientDTO> getAllClients() {
        log.debug("REST request to get all Clients");
        return clientService.findAll();
    }

    /**
     * {@code GET  /clients/:id} : get the "id" client.
     *
     * @param id the id of the clientDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the clientDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") Long id) {
        log.debug("REST request to get Client : {}", id);
        Optional<ClientDTO> clientDTO = clientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientDTO);
    }
}
