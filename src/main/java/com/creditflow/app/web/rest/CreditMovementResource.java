package com.creditflow.app.web.rest;

import static jakarta.ws.rs.core.UriBuilder.fromPath;

import com.creditflow.app.service.CreditMovementService;
import com.creditflow.app.service.Paged;
import com.creditflow.app.service.dto.CreditMovementDTO;
import com.creditflow.app.web.rest.errors.BadRequestAlertException;
import com.creditflow.app.web.rest.vm.PageRequestVM;
import com.creditflow.app.web.rest.vm.SortRequestVM;
import com.creditflow.app.web.util.HeaderUtil;
import com.creditflow.app.web.util.PaginationUtil;
import com.creditflow.app.web.util.ResponseUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing {@link com.creditflow.app.domain.CreditMovement}.
 */
@Path("/api/credit-movements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CreditMovementResource {

    private final Logger log = LoggerFactory.getLogger(CreditMovementResource.class);

    private static final String ENTITY_NAME = "creditMovement";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    CreditMovementService creditMovementService;

    /**
     * {@code POST  /credit-movements} : Create a new creditMovement.
     *
     * @param creditMovementDTO the creditMovementDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new creditMovementDTO, or with status {@code 400 (Bad Request)} if the creditMovement has already an ID.
     */
    @POST
    public Response createCreditMovement(@Valid CreditMovementDTO creditMovementDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save CreditMovement : {}", creditMovementDTO);
        if (creditMovementDTO.id != null) {
            throw new BadRequestAlertException("A new creditMovement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = creditMovementService.persistOrUpdate(creditMovementDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /credit-movements} : Updates an existing creditMovement.
     *
     * @param creditMovementDTO the creditMovementDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated creditMovementDTO,
     * or with status {@code 400 (Bad Request)} if the creditMovementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the creditMovementDTO couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    public Response updateCreditMovement(@Valid CreditMovementDTO creditMovementDTO, @PathParam("id") Long id) {
        log.debug("REST request to update CreditMovement : {}", creditMovementDTO);
        if (creditMovementDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = creditMovementService.persistOrUpdate(creditMovementDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, creditMovementDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /credit-movements/:id} : delete the "id" creditMovement.
     *
     * @param id the id of the creditMovementDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCreditMovement(@PathParam("id") Long id) {
        log.debug("REST request to delete CreditMovement : {}", id);
        creditMovementService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /credit-movements} : get all the creditMovements.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of creditMovements in body.
     */
    @GET
    public Response getAllCreditMovements(
        @BeanParam PageRequestVM pageRequest,
        @BeanParam SortRequestVM sortRequest,
        @Context UriInfo uriInfo
    ) {
        log.debug("REST request to get a page of CreditMovements");
        var page = pageRequest.toPage();
        var sort = sortRequest.toSort();
        Paged<CreditMovementDTO> result = creditMovementService.findAll(page);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }

    /**
     * {@code GET  /credit-movements/:id} : get the "id" creditMovement.
     *
     * @param id the id of the creditMovementDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the creditMovementDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getCreditMovement(@PathParam("id") Long id) {
        log.debug("REST request to get CreditMovement : {}", id);
        Optional<CreditMovementDTO> creditMovementDTO = creditMovementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(creditMovementDTO);
    }
}
