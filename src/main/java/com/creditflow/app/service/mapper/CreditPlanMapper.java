package com.creditflow.app.service.mapper;

import com.creditflow.app.domain.*;
import com.creditflow.app.service.dto.CreditPlanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CreditPlan} and its DTO {@link CreditPlanDTO}.
 */
@Mapper(componentModel = "jakarta", uses = {})
public interface CreditPlanMapper extends EntityMapper<CreditPlanDTO, CreditPlan> {
    @Mapping(target = "loans", ignore = true)
    CreditPlan toEntity(CreditPlanDTO creditPlanDTO);

    default CreditPlan fromId(Long id) {
        if (id == null) {
            return null;
        }
        CreditPlan creditPlan = new CreditPlan();
        creditPlan.id = id;
        return creditPlan;
    }
}
