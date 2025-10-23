package com.creditflow.app.domain;

import static com.creditflow.app.domain.ClientTestSamples.*;
import static com.creditflow.app.domain.CreditMovementTestSamples.*;
import static com.creditflow.app.domain.CreditPlanTestSamples.*;
import static com.creditflow.app.domain.LoanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class LoanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Loan.class);
        Loan loan1 = getLoanSample1();
        Loan loan2 = new Loan();
        assertThat(loan1).isNotEqualTo(loan2);

        loan2.id = loan1.id;
        assertThat(loan1).isEqualTo(loan2);

        loan2 = getLoanSample2();
        assertThat(loan1).isNotEqualTo(loan2);
    }
}
