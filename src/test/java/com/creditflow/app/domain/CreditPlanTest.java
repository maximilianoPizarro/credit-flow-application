package com.creditflow.app.domain;

import static com.creditflow.app.domain.CreditPlanTestSamples.*;
import static com.creditflow.app.domain.LoanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.creditflow.app.TestUtil;
import org.junit.jupiter.api.Test;

class CreditPlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CreditPlan.class);
        CreditPlan creditPlan1 = getCreditPlanSample1();
        CreditPlan creditPlan2 = new CreditPlan();
        assertThat(creditPlan1).isNotEqualTo(creditPlan2);

        creditPlan2.id = creditPlan1.id;
        assertThat(creditPlan1).isEqualTo(creditPlan2);

        creditPlan2 = getCreditPlanSample2();
        assertThat(creditPlan1).isNotEqualTo(creditPlan2);
    }
}
