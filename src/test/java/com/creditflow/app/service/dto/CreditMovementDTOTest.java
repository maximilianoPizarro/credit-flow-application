package com.creditflow.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class CreditMovementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CreditMovementDTO.class);
        CreditMovementDTO creditMovementDTO1 = new CreditMovementDTO();
        creditMovementDTO1.id = 1L;
        CreditMovementDTO creditMovementDTO2 = new CreditMovementDTO();
        assertThat(creditMovementDTO1).isNotEqualTo(creditMovementDTO2);
        creditMovementDTO2.id = creditMovementDTO1.id;
        assertThat(creditMovementDTO1).isEqualTo(creditMovementDTO2);
        creditMovementDTO2.id = 2L;
        assertThat(creditMovementDTO1).isNotEqualTo(creditMovementDTO2);
        creditMovementDTO1.id = null;
        assertThat(creditMovementDTO1).isNotEqualTo(creditMovementDTO2);
    }
}
