package com.creditflow.app.service;

import com.creditflow.app.domain.CreditMovement;
import com.creditflow.app.service.dto.CreditMovementDTO;
import com.creditflow.app.service.mapper.CreditMovementMapper;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class CreditMovementService {

    private final Logger log = LoggerFactory.getLogger(CreditMovementService.class);

    @Inject
    CreditMovementMapper creditMovementMapper;

    @Transactional
    public CreditMovementDTO persistOrUpdate(CreditMovementDTO creditMovementDTO) {
        log.debug("Request to save CreditMovement : {}", creditMovementDTO);
        var creditMovement = creditMovementMapper.toEntity(creditMovementDTO);
        creditMovement = CreditMovement.persistOrUpdate(creditMovement);
        return creditMovementMapper.toDto(creditMovement);
    }

    /**
     * Delete the CreditMovement by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete CreditMovement : {}", id);
        CreditMovement.findByIdOptional(id).ifPresent(creditMovement -> {
            creditMovement.delete();
        });
    }

    /**
     * Get one creditMovement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<CreditMovementDTO> findOne(Long id) {
        log.debug("Request to get CreditMovement : {}", id);
        return CreditMovement.findByIdOptional(id).map(creditMovement -> creditMovementMapper.toDto((CreditMovement) creditMovement));
    }

    /**
     * Get all the creditMovements.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<CreditMovementDTO> findAll(Page page) {
        log.debug("Request to get all CreditMovements");
        return new Paged<>(CreditMovement.findAll().page(page)).map(creditMovement ->
            creditMovementMapper.toDto((CreditMovement) creditMovement)
        );
    }
}
