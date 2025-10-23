package com.creditflow.app.service.dto;

import com.creditflow.app.domain.enumeration.MovementType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * A DTO for the {@link com.creditflow.app.domain.CreditMovement} entity.
 */
@ApiModel(description = "Represents a transaction on a loan (payment, charge, installment, etc.).")
@RegisterForReflection
public class CreditMovementDTO implements Serializable {

    public Long id;

    @NotNull
    public ZonedDateTime movementDate;

    @NotNull
    public MovementType type;

    @NotNull
    public BigDecimal amount;

    public String description;

    public String externalReference;

    public Long loanId;
    public String loan;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditMovementDTO)) {
            return false;
        }

        return id != null && id.equals(((CreditMovementDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "CreditMovementDTO{" +
            ", id=" +
            id +
            ", movementDate='" +
            movementDate +
            "'" +
            ", type='" +
            type +
            "'" +
            ", amount=" +
            amount +
            ", description='" +
            description +
            "'" +
            ", externalReference='" +
            externalReference +
            "'" +
            ", loanId=" +
            loanId +
            ", loan='" +
            loan +
            "'" +
            "}"
        );
    }
}
