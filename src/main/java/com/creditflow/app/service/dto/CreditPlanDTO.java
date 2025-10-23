package com.creditflow.app.service.dto;

import com.creditflow.app.domain.enumeration.PlanType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;

/**
 * A DTO for the {@link com.creditflow.app.domain.CreditPlan} entity.
 */
@ApiModel(description = "Represents a credit product or plan offered by the bank.")
@RegisterForReflection
public class CreditPlanDTO implements Serializable {

    public Long id;

    @NotNull
    public String name;

    @Lob
    public String description;

    @NotNull
    public PlanType type;

    @NotNull
    public BigDecimal interestRate;

    public Integer maxTermYears;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditPlanDTO)) {
            return false;
        }

        return id != null && id.equals(((CreditPlanDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "CreditPlanDTO{" +
            ", id=" +
            id +
            ", name='" +
            name +
            "'" +
            ", description='" +
            description +
            "'" +
            ", type='" +
            type +
            "'" +
            ", interestRate=" +
            interestRate +
            ", maxTermYears=" +
            maxTermYears +
            "}"
        );
    }
}
