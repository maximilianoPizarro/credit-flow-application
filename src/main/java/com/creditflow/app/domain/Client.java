package com.creditflow.app.domain;

import com.creditflow.app.domain.enumeration.DocumentType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a bank customer.
 */
@Entity
@Table(name = "client")
@RegisterForReflection
public class Client extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "client_number", nullable = false, unique = true)
    public Long clientNumber;

    @NotNull
    @Column(name = "first_name", nullable = false)
    public String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    public String lastName;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    public LocalDate dateOfBirth;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    public DocumentType documentType;

    @NotNull
    @Column(name = "document_number", nullable = false, unique = true)
    public String documentNumber;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "phone")
    public String phone;

    @OneToMany(mappedBy = "client")
    public Set<Loan> loans = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "Client{" +
            "id=" +
            id +
            ", clientNumber=" +
            clientNumber +
            ", firstName='" +
            firstName +
            "'" +
            ", lastName='" +
            lastName +
            "'" +
            ", dateOfBirth='" +
            dateOfBirth +
            "'" +
            ", documentType='" +
            documentType +
            "'" +
            ", documentNumber='" +
            documentNumber +
            "'" +
            ", email='" +
            email +
            "'" +
            ", phone='" +
            phone +
            "'" +
            "}"
        );
    }

    public Client update() {
        return update(this);
    }

    public Client persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Client update(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("client can't be null");
        }
        var entity = Client.<Client>findById(client.id);
        if (entity != null) {
            entity.clientNumber = client.clientNumber;
            entity.firstName = client.firstName;
            entity.lastName = client.lastName;
            entity.dateOfBirth = client.dateOfBirth;
            entity.documentType = client.documentType;
            entity.documentNumber = client.documentNumber;
            entity.email = client.email;
            entity.phone = client.phone;
            entity.loans = client.loans;
        }
        return entity;
    }

    public static Client persistOrUpdate(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("client can't be null");
        }
        if (client.id == null) {
            persist(client);
            return client;
        } else {
            return update(client);
        }
    }
}
