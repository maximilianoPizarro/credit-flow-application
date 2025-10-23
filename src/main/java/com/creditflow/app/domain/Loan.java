package com.creditflow.app.domain;

import com.creditflow.app.domain.enumeration.LoanStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a specific loan or financial product
 * that a client owns. It is the core of the credit history.
 */
@Entity
@Table(name = "loan")
@RegisterForReflection
public class Loan extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "loan_number", nullable = false, unique = true)
    public String loanNumber;

    @NotNull
    @Column(name = "opening_date", nullable = false)
    public ZonedDateTime openingDate;

    @NotNull
    @Column(name = "principal_amount", precision = 21, scale = 2, nullable = false)
    public BigDecimal principalAmount;

    @Column(name = "outstanding_balance", precision = 21, scale = 2)
    public BigDecimal outstandingBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public LoanStatus status;

    @Column(name = "closing_date")
    public ZonedDateTime closingDate;

    @OneToMany(mappedBy = "loan")
    public Set<CreditMovement> movements = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "client_id")
    public Client client;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    public CreditPlan plan;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Loan)) {
            return false;
        }
        return id != null && id.equals(((Loan) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "Loan{" +
            "id=" +
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
            "}"
        );
    }

    public Loan update() {
        return update(this);
    }

    public Loan persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Loan update(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan can't be null");
        }
        var entity = Loan.<Loan>findById(loan.id);
        if (entity != null) {
            entity.loanNumber = loan.loanNumber;
            entity.openingDate = loan.openingDate;
            entity.principalAmount = loan.principalAmount;
            entity.outstandingBalance = loan.outstandingBalance;
            entity.status = loan.status;
            entity.closingDate = loan.closingDate;
            entity.movements = loan.movements;
            entity.client = loan.client;
            entity.plan = loan.plan;
        }
        return entity;
    }

    public static Loan persistOrUpdate(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("loan can't be null");
        }
        if (loan.id == null) {
            persist(loan);
            return loan;
        } else {
            return update(loan);
        }
    }
}
