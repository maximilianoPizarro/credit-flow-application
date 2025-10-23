package com.creditflow.app.domain;

import com.creditflow.app.domain.enumeration.PlanType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a credit product or plan offered by the bank.
 */
@Entity
@Table(name = "credit_plan")
@RegisterForReflection
public class CreditPlan extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Lob
    @Column(name = "description")
    public String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public PlanType type;

    @NotNull
    @Column(name = "interest_rate", precision = 21, scale = 2, nullable = false)
    public BigDecimal interestRate;

    @Column(name = "max_term_years")
    public Integer maxTermYears;

    @OneToMany(mappedBy = "plan")
    public Set<Loan> loans = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditPlan)) {
            return false;
        }
        return id != null && id.equals(((CreditPlan) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "CreditPlan{" +
            "id=" +
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

    public CreditPlan update() {
        return update(this);
    }

    public CreditPlan persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static CreditPlan update(CreditPlan creditPlan) {
        if (creditPlan == null) {
            throw new IllegalArgumentException("creditPlan can't be null");
        }
        var entity = CreditPlan.<CreditPlan>findById(creditPlan.id);
        if (entity != null) {
            entity.name = creditPlan.name;
            entity.description = creditPlan.description;
            entity.type = creditPlan.type;
            entity.interestRate = creditPlan.interestRate;
            entity.maxTermYears = creditPlan.maxTermYears;
            entity.loans = creditPlan.loans;
        }
        return entity;
    }

    public static CreditPlan persistOrUpdate(CreditPlan creditPlan) {
        if (creditPlan == null) {
            throw new IllegalArgumentException("creditPlan can't be null");
        }
        if (creditPlan.id == null) {
            persist(creditPlan);
            return creditPlan;
        } else {
            return update(creditPlan);
        }
    }
}
