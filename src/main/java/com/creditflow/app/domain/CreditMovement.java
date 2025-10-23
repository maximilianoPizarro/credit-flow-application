package com.creditflow.app.domain;

import com.creditflow.app.domain.enumeration.MovementType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Represents a transaction on a loan (payment, charge, installment, etc.).
 */
@Entity
@Table(name = "credit_movement")
@RegisterForReflection
public class CreditMovement extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "movement_date", nullable = false)
    public ZonedDateTime movementDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public MovementType type;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    public BigDecimal amount;

    @Column(name = "description")
    public String description;

    @Column(name = "external_reference", unique = true)
    public String externalReference;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    public Loan loan;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditMovement)) {
            return false;
        }
        return id != null && id.equals(((CreditMovement) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "CreditMovement{" +
            "id=" +
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
            "}"
        );
    }

    public CreditMovement update() {
        return update(this);
    }

    public CreditMovement persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static CreditMovement update(CreditMovement creditMovement) {
        if (creditMovement == null) {
            throw new IllegalArgumentException("creditMovement can't be null");
        }
        var entity = CreditMovement.<CreditMovement>findById(creditMovement.id);
        if (entity != null) {
            entity.movementDate = creditMovement.movementDate;
            entity.type = creditMovement.type;
            entity.amount = creditMovement.amount;
            entity.description = creditMovement.description;
            entity.externalReference = creditMovement.externalReference;
            entity.loan = creditMovement.loan;
        }
        return entity;
    }

    public static CreditMovement persistOrUpdate(CreditMovement creditMovement) {
        if (creditMovement == null) {
            throw new IllegalArgumentException("creditMovement can't be null");
        }
        if (creditMovement.id == null) {
            persist(creditMovement);
            return creditMovement;
        } else {
            return update(creditMovement);
        }
    }
}
