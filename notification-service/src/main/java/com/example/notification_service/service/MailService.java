package com.example.notification_service.service;

import com.example.notification_service.model.PaymentStatusEvent;

public interface MailService {
    void sendPaymentMailToAdmin(PaymentStatusEvent event);

    void sendFailedPaymentMailToAdmin(PaymentStatusEvent event);
}
