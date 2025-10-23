package com.creditflow.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Client getClientSample1() {
        Client client = new Client();
        client.id = 1L;
        client.clientNumber = 1L;
        client.firstName = "firstName1";
        client.lastName = "lastName1";
        client.documentNumber = "documentNumber1";
        client.email = "email1";
        client.phone = "phone1";
        return client;
    }

    public static Client getClientSample2() {
        Client client = new Client();
        client.id = 2L;
        client.clientNumber = 2L;
        client.firstName = "firstName2";
        client.lastName = "lastName2";
        client.documentNumber = "documentNumber2";
        client.email = "email2";
        client.phone = "phone2";
        return client;
    }

    public static Client getClientRandomSampleGenerator() {
        Client client = new Client();
        client.id = longCount.incrementAndGet();
        client.clientNumber = longCount.incrementAndGet();
        client.firstName = UUID.randomUUID().toString();
        client.lastName = UUID.randomUUID().toString();
        client.documentNumber = UUID.randomUUID().toString();
        client.email = UUID.randomUUID().toString();
        client.phone = UUID.randomUUID().toString();
        return client;
    }
}
