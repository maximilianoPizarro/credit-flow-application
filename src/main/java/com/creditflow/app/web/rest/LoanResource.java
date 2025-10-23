package com.creditflow.app.web.rest;

import static jakarta.ws.rs.core.UriBuilder.fromPath;

import com.creditflow.app.service.LoanService;
import com.creditflow.app.service.Paged;
import com.creditflow.app.service.dto.LoanDTO;
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
 * REST controller for managing {@link com.creditflow.app.domain.Loan}.
 */
@Path("/api/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class LoanResource {

    private final Logger log = LoggerFactory.getLogger(LoanResource.class);

    private static final String ENTITY_NAME = "loan";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    LoanService loanService;

    /**
     * {@code POST  /loans} : Create a new loan.
     *
     * @param loanDTO the loanDTO to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new loanDTO, or with status {@code 400 (Bad Request)} if the loan has already an ID.
     */
    @POST
    public Response createLoan(@Valid LoanDTO loanDTO, @Context UriInfo uriInfo) {
        log.debug("REST request to save Loan : {}", loanDTO);
        if (loanDTO.id != null) {
            throw new BadRequestAlertException("A new loan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = loanService.persistOrUpdate(loanDTO);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /loans} : Updates an existing loan.
     *
     * @param loanDTO the loanDTO to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated loanDTO,
     * or with status {@code 400 (Bad Request)} if the loanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the loanDTO couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    public Response updateLoan(@Valid LoanDTO loanDTO, @PathParam("id") Long id) {
        log.debug("REST request to update Loan : {}", loanDTO);
        if (loanDTO.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = loanService.persistOrUpdate(loanDTO);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, loanDTO.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /loans/:id} : delete the "id" loan.
     *
     * @param id the id of the loanDTO to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteLoan(@PathParam("id") Long id) {
        log.debug("REST request to delete Loan : {}", id);
        loanService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /loans} : get all the loans.
     *
     * @param pageRequest the pagination information.
     * @return the {@link Response} with status {@code 200 (OK)} and the list of loans in body.
     */
    @GET
    public Response getAllLoans(@BeanParam PageRequestVM pageRequest, @BeanParam SortRequestVM sortRequest, @Context UriInfo uriInfo) {
        log.debug("REST request to get a page of Loans");
        var page = pageRequest.toPage();
        var sort = sortRequest.toSort();
        Paged<LoanDTO> result = loanService.findAll(page);
        var response = Response.ok().entity(result.content);
        response = PaginationUtil.withPaginationInfo(response, uriInfo, result);
        return response.build();
    }

    /**
     * {@code GET  /loans/:id} : get the "id" loan.
     *
     * @param id the id of the loanDTO to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the loanDTO, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getLoan(@PathParam("id") Long id) {
        log.debug("REST request to get Loan : {}", id);
        Optional<LoanDTO> loanDTO = loanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(loanDTO);
    }
}
