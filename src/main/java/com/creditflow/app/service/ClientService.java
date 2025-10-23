package com.creditflow.app.service;

import com.creditflow.app.domain.Client;
import com.creditflow.app.service.dto.ClientDTO;
import com.creditflow.app.service.mapper.ClientMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class ClientService {

    private final Logger log = LoggerFactory.getLogger(ClientService.class);

    @Inject
    ClientMapper clientMapper;

    @Transactional
    public ClientDTO persistOrUpdate(ClientDTO clientDTO) {
        log.debug("Request to save Client : {}", clientDTO);
        var client = clientMapper.toEntity(clientDTO);
        client = Client.persistOrUpdate(client);
        return clientMapper.toDto(client);
    }

    /**
     * Delete the Client by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Client : {}", id);
        Client.findByIdOptional(id).ifPresent(client -> {
            client.delete();
        });
    }

    /**
     * Get one client by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ClientDTO> findOne(Long id) {
        log.debug("Request to get Client : {}", id);
        return Client.findByIdOptional(id).map(client -> clientMapper.toDto((Client) client));
    }

    /**
     * Get all the clients.
     * @return the list of entities.
     */
    public List<ClientDTO> findAll() {
        log.debug("Request to get all Clients");
        List<Client> clients = Client.findAll().list();
        return clientMapper.toDto(clients);
    }
}
