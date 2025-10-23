package com.creditflow.app.service.mapper;

import static com.creditflow.app.domain.LoanAsserts.*;
import static com.creditflow.app.domain.LoanTestSamples.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class LoanMapperTest {

    @Inject
    LoanMapper loanMapper;

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLoanSample1();
        var actual = loanMapper.toEntity(loanMapper.toDto(expected));
        assertLoanAllPropertiesEquals(expected, actual);
    }
}
