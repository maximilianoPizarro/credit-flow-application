package com.creditflow.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CreditMovementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CreditMovement getCreditMovementSample1() {
        CreditMovement creditMovement = new CreditMovement();
        creditMovement.id = 1L;
        creditMovement.description = "description1";
        creditMovement.externalReference = "externalReference1";
        return creditMovement;
    }

    public static CreditMovement getCreditMovementSample2() {
        CreditMovement creditMovement = new CreditMovement();
        creditMovement.id = 2L;
        creditMovement.description = "description2";
        creditMovement.externalReference = "externalReference2";
        return creditMovement;
    }

    public static CreditMovement getCreditMovementRandomSampleGenerator() {
        CreditMovement creditMovement = new CreditMovement();
        creditMovement.id = longCount.incrementAndGet();
        creditMovement.description = UUID.randomUUID().toString();
        creditMovement.externalReference = UUID.randomUUID().toString();
        return creditMovement;
    }
}
