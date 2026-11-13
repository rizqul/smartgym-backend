package com.gymmick.smartgym.services.email;

import java.util.Map;

import com.gymmick.smartgym.exceptions.EmailSenderException;

public interface EmailService {

    void sendHtmlEmail(String template, String subject, String recipient, Map<String, Object> variables)
            throws EmailSenderException;
}
