package com.microservices.pharmacare.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private boolean isInitialized = false;

    private void initializeTwilio() {
        if (!isInitialized) {
            Twilio.init(accountSid, authToken);
            isInitialized = true;
        }
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        initializeTwilio(); // Ensure Twilio is initialized before sending SMS
        Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber), // To number
                new com.twilio.type.PhoneNumber(twilioPhoneNumber), // From Twilio number
                messageBody // SMS Body
        ).create();
    }
}
