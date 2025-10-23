package com.creditflow.app.service.mapper;

import static com.creditflow.app.domain.CreditMovementAsserts.*;
import static com.creditflow.app.domain.CreditMovementTestSamples.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CreditMovementMapperTest {

    @Inject
    CreditMovementMapper creditMovementMapper;

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCreditMovementSample1();
        var actual = creditMovementMapper.toEntity(creditMovementMapper.toDto(expected));
        assertCreditMovementAllPropertiesEquals(expected, actual);
    }
}
