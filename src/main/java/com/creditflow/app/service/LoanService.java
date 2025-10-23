package com.creditflow.app.service;

import com.creditflow.app.domain.Loan;
import com.creditflow.app.service.dto.LoanDTO;
import com.creditflow.app.service.mapper.LoanMapper;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class LoanService {

    private final Logger log = LoggerFactory.getLogger(LoanService.class);

    @Inject
    LoanMapper loanMapper;

    @Transactional
    public LoanDTO persistOrUpdate(LoanDTO loanDTO) {
        log.debug("Request to save Loan : {}", loanDTO);
        var loan = loanMapper.toEntity(loanDTO);
        loan = Loan.persistOrUpdate(loan);
        return loanMapper.toDto(loan);
    }

    /**
     * Delete the Loan by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Loan : {}", id);
        Loan.findByIdOptional(id).ifPresent(loan -> {
            loan.delete();
        });
    }

    /**
     * Get one loan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<LoanDTO> findOne(Long id) {
        log.debug("Request to get Loan : {}", id);
        return Loan.findByIdOptional(id).map(loan -> loanMapper.toDto((Loan) loan));
    }

    /**
     * Get all the loans.
     * @param page the pagination information.
     * @return the list of entities.
     */
    public Paged<LoanDTO> findAll(Page page) {
        log.debug("Request to get all Loans");
        return new Paged<>(Loan.findAll().page(page)).map(loan -> loanMapper.toDto((Loan) loan));
    }
}
