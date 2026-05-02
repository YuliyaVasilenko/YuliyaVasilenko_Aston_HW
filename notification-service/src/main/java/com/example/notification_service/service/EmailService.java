package com.example.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 17-04-2026
 * Description: the service for sending emails
 */
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * @ Method Name: send
     * @ Description: sends an email message
     * @ param      : [java.lang.String, java.lang.String, java.lang.String]
     * @ return     : void
     */
    public void send(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
