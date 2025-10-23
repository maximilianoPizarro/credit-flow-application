package com.creditflow.app.service.dto;

import com.creditflow.app.config.Constants;
import com.creditflow.app.domain.enumeration.DocumentType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.creditflow.app.domain.Client} entity.
 */
@ApiModel(description = "Represents a bank customer.")
@RegisterForReflection
public class ClientDTO implements Serializable {

    public Long id;

    @NotNull
    public Long clientNumber;

    @NotNull
    public String firstName;

    @NotNull
    public String lastName;

    @NotNull
    @JsonbDateFormat(value = Constants.LOCAL_DATE_FORMAT)
    public LocalDate dateOfBirth;

    @NotNull
    public DocumentType documentType;

    @NotNull
    public String documentNumber;

    public String email;

    public String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientDTO)) {
            return false;
        }

        return id != null && id.equals(((ClientDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "ClientDTO{" +
            ", id=" +
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
}
