package com.creditflow.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class LoanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LoanDTO.class);
        LoanDTO loanDTO1 = new LoanDTO();
        loanDTO1.id = 1L;
        LoanDTO loanDTO2 = new LoanDTO();
        assertThat(loanDTO1).isNotEqualTo(loanDTO2);
        loanDTO2.id = loanDTO1.id;
        assertThat(loanDTO1).isEqualTo(loanDTO2);
        loanDTO2.id = 2L;
        assertThat(loanDTO1).isNotEqualTo(loanDTO2);
        loanDTO1.id = null;
        assertThat(loanDTO1).isNotEqualTo(loanDTO2);
    }
}
