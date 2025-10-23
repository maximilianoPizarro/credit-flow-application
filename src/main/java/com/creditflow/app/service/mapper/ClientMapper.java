package com.creditflow.app.service.mapper;

import com.creditflow.app.domain.*;
import com.creditflow.app.service.dto.ClientDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "jakarta", uses = {})
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {
    @Mapping(target = "loans", ignore = true)
    Client toEntity(ClientDTO clientDTO);

    default Client fromId(Long id) {
        if (id == null) {
            return null;
        }
        Client client = new Client();
        client.id = id;
        return client;
    }
}
