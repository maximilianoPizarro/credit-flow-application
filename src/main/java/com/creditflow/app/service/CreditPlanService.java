package com.creditflow.app.service;

import com.creditflow.app.domain.CreditPlan;
import com.creditflow.app.service.dto.CreditPlanDTO;
import com.creditflow.app.service.mapper.CreditPlanMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class CreditPlanService {

    private final Logger log = LoggerFactory.getLogger(CreditPlanService.class);

    @Inject
    CreditPlanMapper creditPlanMapper;

    @Transactional
    public CreditPlanDTO persistOrUpdate(CreditPlanDTO creditPlanDTO) {
        log.debug("Request to save CreditPlan : {}", creditPlanDTO);
        var creditPlan = creditPlanMapper.toEntity(creditPlanDTO);
        creditPlan = CreditPlan.persistOrUpdate(creditPlan);
        return creditPlanMapper.toDto(creditPlan);
    }

    /**
     * Delete the CreditPlan by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete CreditPlan : {}", id);
        CreditPlan.findByIdOptional(id).ifPresent(creditPlan -> {
            creditPlan.delete();
        });
    }

    /**
     * Get one creditPlan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<CreditPlanDTO> findOne(Long id) {
        log.debug("Request to get CreditPlan : {}", id);
        return CreditPlan.findByIdOptional(id).map(creditPlan -> creditPlanMapper.toDto((CreditPlan) creditPlan));
    }

    /**
     * Get all the creditPlans.
     * @return the list of entities.
     */
    public List<CreditPlanDTO> findAll() {
        log.debug("Request to get all CreditPlans");
        List<CreditPlan> creditPlans = CreditPlan.findAll().list();
        return creditPlanMapper.toDto(creditPlans);
    }
}
