package com.creditflow.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LoanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Loan getLoanSample1() {
        Loan loan = new Loan();
        loan.id = 1L;
        loan.loanNumber = "loanNumber1";
        return loan;
    }

    public static Loan getLoanSample2() {
        Loan loan = new Loan();
        loan.id = 2L;
        loan.loanNumber = "loanNumber2";
        return loan;
    }

    public static Loan getLoanRandomSampleGenerator() {
        Loan loan = new Loan();
        loan.id = longCount.incrementAndGet();
        loan.loanNumber = UUID.randomUUID().toString();
        return loan;
    }
}
