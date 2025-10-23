package com.creditflow.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class CreditPlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CreditPlanDTO.class);
        CreditPlanDTO creditPlanDTO1 = new CreditPlanDTO();
        creditPlanDTO1.id = 1L;
        CreditPlanDTO creditPlanDTO2 = new CreditPlanDTO();
        assertThat(creditPlanDTO1).isNotEqualTo(creditPlanDTO2);
        creditPlanDTO2.id = creditPlanDTO1.id;
        assertThat(creditPlanDTO1).isEqualTo(creditPlanDTO2);
        creditPlanDTO2.id = 2L;
        assertThat(creditPlanDTO1).isNotEqualTo(creditPlanDTO2);
        creditPlanDTO1.id = null;
        assertThat(creditPlanDTO1).isNotEqualTo(creditPlanDTO2);
    }
}
