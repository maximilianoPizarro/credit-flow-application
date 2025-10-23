package com.creditflow.app.service.mapper;

import static com.creditflow.app.domain.CreditPlanAsserts.*;
import static com.creditflow.app.domain.CreditPlanTestSamples.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CreditPlanMapperTest {

    @Inject
    CreditPlanMapper creditPlanMapper;

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCreditPlanSample1();
        var actual = creditPlanMapper.toEntity(creditPlanMapper.toDto(expected));
        assertCreditPlanAllPropertiesEquals(expected, actual);
    }
}
