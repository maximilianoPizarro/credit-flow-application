package com.creditflow.app.domain;

import static com.creditflow.app.domain.ClientTestSamples.*;
import static com.creditflow.app.domain.LoanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Client.class);
        Client client1 = getClientSample1();
        Client client2 = new Client();
        assertThat(client1).isNotEqualTo(client2);

        client2.id = client1.id;
        assertThat(client1).isEqualTo(client2);

        client2 = getClientSample2();
        assertThat(client1).isNotEqualTo(client2);
    }
}
