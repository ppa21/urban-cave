package com.urbancave.service;

import com.urbancave.domain.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendConfirmation(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(appointment.getClientEmail());
            message.setSubject("Urban Cave: Appointment Confirmed");
            message.setText(appointment.getClientName() + "'s Appointment with " +
                    appointment.getStylist().getName() + " is confirmed for " +
                    appointment.getStartTime());
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Email failed", e);
        }
    }
}
