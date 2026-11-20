package com.gymmick.smartgym.services.utils.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.gymmick.smartgym.exceptions.EmailSenderException;
import com.gymmick.smartgym.services.utils.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Async
    @Override
    public void sendHtmlEmail(String template, String subject, String recipient, Map<String, Object> variables)
            throws EmailSenderException {

        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlEmail = templateEngine.process(template, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mailMessage = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            mailMessage.setSubject(subject);
            mailMessage.setFrom(emailFrom, "SmartGym");

            mailMessage.setTo(recipient);
            mailMessage.setText(htmlEmail, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException exception) {
            throw new EmailSenderException(exception.getMessage());
        }
    }
}
