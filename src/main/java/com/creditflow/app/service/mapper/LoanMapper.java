package com.creditflow.app.service.mapper;

import com.creditflow.app.domain.*;
import com.creditflow.app.service.dto.LoanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Loan} and its DTO {@link LoanDTO}.
 */
@Mapper(componentModel = "jakarta", uses = { ClientMapper.class, CreditPlanMapper.class })
public interface LoanMapper extends EntityMapper<LoanDTO, Loan> {
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.documentNumber", target = "client")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "plan")
    LoanDTO toDto(Loan loan);

    @Mapping(target = "movements", ignore = true)
    @Mapping(source = "clientId", target = "client")
    @Mapping(source = "planId", target = "plan")
    Loan toEntity(LoanDTO loanDTO);

    default Loan fromId(Long id) {
        if (id == null) {
            return null;
        }
        Loan loan = new Loan();
        loan.id = id;
        return loan;
    }
}
