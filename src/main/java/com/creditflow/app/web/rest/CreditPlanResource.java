package com.creditflow.app.web.rest;

import static jakarta.ws.rs.core.UriBuilder.fromPath;

import com.creditflow.app.service.CreditPlanService;
import com.creditflow.app.service.dto.CreditPlanDTO;
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
 * REST controller for managing {@link com.creditflow.app.domain.CreditPlan}.
 */
@Path("/api/credit-plans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CreditPlanResource {

    private final Logger log = LoggerFactory.getLogger(CreditPlanResource.class);

    private static final String ENTITY_NAME = "creditPlan";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    CreditPlanService creditPlanService;

    /**
     * {@code POST  /credit-plans} : Create a new creditPlan.
     *
     * @param creditPlanDTO the creditPlanDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new creditPlanDTO, or with status {@code 400 (Bad Request)} if the creditPlan has already an ID.
     */
    @POST
    public Response createCreditPlan(@Valid CreditPlanDTO creditPlanDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save CreditPlan : {}", creditPlanDTO);
        if (creditPlanDTO.id != null) {
            throw new BadRequestAlertException("A new creditPlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = creditPlanService.persistOrUpdate(creditPlanDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /credit-plans} : Updates an existing creditPlan.
     *
     * @param creditPlanDTO the creditPlanDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated creditPlanDTO,
     * or with status {@code 400 (Bad Request)} if the creditPlanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the creditPlanDTO couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    public Response updateCreditPlan(@Valid CreditPlanDTO creditPlanDTO, @PathParam("id") Long id) {
        log.debug("REST request to update CreditPlan : {}", creditPlanDTO);
        if (creditPlanDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = creditPlanService.persistOrUpdate(creditPlanDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, creditPlanDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /credit-plans/:id} : delete the "id" creditPlan.
     *
     * @param id the id of the creditPlanDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCreditPlan(@PathParam("id") Long id) {
        log.debug("REST request to delete CreditPlan : {}", id);
        creditPlanService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /credit-plans} : get all the creditPlans.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of creditPlans in body.
     */
    @GET
    public List<CreditPlanDTO> getAllCreditPlans() {
        log.debug("REST request to get all CreditPlans");
        return creditPlanService.findAll();
    }

    /**
     * {@code GET  /credit-plans/:id} : get the "id" creditPlan.
     *
     * @param id the id of the creditPlanDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the creditPlanDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getCreditPlan(@PathParam("id") Long id) {
        log.debug("REST request to get CreditPlan : {}", id);
        Optional<CreditPlanDTO> creditPlanDTO = creditPlanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(creditPlanDTO);
    }
}
