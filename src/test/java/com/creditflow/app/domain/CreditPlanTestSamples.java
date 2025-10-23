package com.creditflow.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CreditPlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CreditPlan getCreditPlanSample1() {
        CreditPlan creditPlan = new CreditPlan();
        creditPlan.id = 1L;
        creditPlan.name = "name1";
        creditPlan.maxTermYears = 1;
        return creditPlan;
    }

    public static CreditPlan getCreditPlanSample2() {
        CreditPlan creditPlan = new CreditPlan();
        creditPlan.id = 2L;
        creditPlan.name = "name2";
        creditPlan.maxTermYears = 2;
        return creditPlan;
    }

    public static CreditPlan getCreditPlanRandomSampleGenerator() {
        CreditPlan creditPlan = new CreditPlan();
        creditPlan.id = longCount.incrementAndGet();
        creditPlan.name = UUID.randomUUID().toString();
        creditPlan.maxTermYears = intCount.incrementAndGet();
        return creditPlan;
    }
}
