package com.creditflow.app.service.mapper;

import com.creditflow.app.domain.*;
import com.creditflow.app.service.dto.CreditMovementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CreditMovement} and its DTO {@link CreditMovementDTO}.
 */
@Mapper(componentModel = "jakarta", uses = { LoanMapper.class })
public interface CreditMovementMapper extends EntityMapper<CreditMovementDTO, CreditMovement> {
    @Mapping(source = "loan.id", target = "loanId")
    @Mapping(source = "loan.loanNumber", target = "loan")
    CreditMovementDTO toDto(CreditMovement creditMovement);

    @Mapping(source = "loanId", target = "loan")
    CreditMovement toEntity(CreditMovementDTO creditMovementDTO);

    default CreditMovement fromId(Long id) {
        if (id == null) {
            return null;
        }
        CreditMovement creditMovement = new CreditMovement();
        creditMovement.id = id;
        return creditMovement;
    }
}
