package com.example.notification_service.service.impl;

import com.example.notification_service.model.PaymentStatusEvent;
import com.example.notification_service.service.MailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendPaymentMailToAdmin(PaymentStatusEvent event) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("nguyenquangtruong2503@gmail.com");   // gửi thông báo tới mail của admin khi có đơn hàng
        mail.setSubject("Payment Completed");
        mail.setText(
                "Order ID: " + event.getOrderId() + "\n" +
                        "Amount: " + event.getAmount() + "\n" +
                        "Status: " + event.getStatus()
        );

        mailSender.send(mail);
    }

    @Override
    public void sendFailedPaymentMailToAdmin(PaymentStatusEvent event) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("nguyenquangtruong2503@gmail.com");   // gửi thông báo tới mail của admin khi hủy đơn
        mail.setSubject("Hủy đơn hàng do chưa thanh toán");
        mail.setText(
                "Order ID: " + event.getOrderId() + "\n" +
                        "Amount: " + event.getAmount() + "\n" +
                        "Status: " + event.getStatus()
        );
        mailSender.send(mail);
    }
}
