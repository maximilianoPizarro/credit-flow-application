package com.creditflow.app.domain;

import static com.creditflow.app.domain.CreditMovementTestSamples.*;
import static com.creditflow.app.domain.LoanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class CreditMovementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CreditMovement.class);
        CreditMovement creditMovement1 = getCreditMovementSample1();
        CreditMovement creditMovement2 = new CreditMovement();
        assertThat(creditMovement1).isNotEqualTo(creditMovement2);

        creditMovement2.id = creditMovement1.id;
        assertThat(creditMovement1).isEqualTo(creditMovement2);

        creditMovement2 = getCreditMovementSample2();
        assertThat(creditMovement1).isNotEqualTo(creditMovement2);
    }
}
