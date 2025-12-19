package com.example.notification_service.kafka;

import com.example.notification_service.model.PaymentStatusEvent;
import com.example.notification_service.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStatusConsumer {

    @Autowired
    private MailService mailService;

    @KafkaListener(topics = "payment.completed", groupId = "notification-group")
    public void listenSuccessPayment(PaymentStatusEvent event) {
        try {
            mailService.sendPaymentMailToAdmin(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-group")
    public void listenFailedPayment(PaymentStatusEvent event) {
        try {
            mailService.sendFailedPaymentMailToAdmin(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
