package com.ensf614.ticketr.service;

import java.util.UUID;

public class PaymentService {

    private static PaymentService instance = null;

    private PaymentService() {
    }

    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    public String processPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        String transtaction_id = UUID.randomUUID().toString();
        
        return transtaction_id;
    }
}
