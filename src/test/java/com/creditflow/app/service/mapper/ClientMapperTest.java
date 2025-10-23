package com.creditflow.app.service.mapper;

import static com.creditflow.app.domain.ClientAsserts.*;
import static com.creditflow.app.domain.ClientTestSamples.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClientMapperTest {

    @Inject
    ClientMapper clientMapper;

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClientSample1();
        var actual = clientMapper.toEntity(clientMapper.toDto(expected));
        assertClientAllPropertiesEquals(expected, actual);
    }
}
