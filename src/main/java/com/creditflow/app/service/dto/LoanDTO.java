package com.creditflow.app.service.dto;

import com.creditflow.app.domain.enumeration.LoanStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import io.swagger.annotations.ApiModel;
/**
 * A DTO for the {@link com.creditflow.app.domain.Loan} entity.
 */
@ApiModel(description = "Represents a specific loan or financial product\nthat a client owns. It is the core of the credit history.")
@RegisterForReflection
public class LoanDTO implements Serializable {

    public Long id;

    @NotNull
    public String loanNumber;

    @NotNull
    public ZonedDateTime openingDate;

    @NotNull
    public BigDecimal principalAmount;

    public BigDecimal outstandingBalance;

    @NotNull
    public LoanStatus status;

    public ZonedDateTime closingDate;

    public Long clientId;
    public String client;
    public Long planId;
    public String plan;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoanDTO)) {
            return false;
        }

        return id != null && id.equals(((LoanDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "LoanDTO{" +
            ", id=" +
            id +
            ", loanNumber='" +
            loanNumber +
            "'" +
            ", openingDate='" +
            openingDate +
            "'" +
            ", principalAmount=" +
            principalAmount +
            ", outstandingBalance=" +
            outstandingBalance +
            ", status='" +
            status +
            "'" +
            ", closingDate='" +
            closingDate +
            "'" +
            ", clientId=" +
            clientId +
            ", client='" +
            client +
            "'" +
            ", planId=" +
            planId +
            ", plan='" +
            plan +
            "'" +
            "}"
        );
    }
}
