package com.creditflow.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class ClientDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientDTO.class);
        ClientDTO clientDTO1 = new ClientDTO();
        clientDTO1.id = 1L;
        ClientDTO clientDTO2 = new ClientDTO();
        assertThat(clientDTO1).isNotEqualTo(clientDTO2);
        clientDTO2.id = clientDTO1.id;
        assertThat(clientDTO1).isEqualTo(clientDTO2);
        clientDTO2.id = 2L;
        assertThat(clientDTO1).isNotEqualTo(clientDTO2);
        clientDTO1.id = null;
        assertThat(clientDTO1).isNotEqualTo(clientDTO2);
    }
}
