package com.seguros.app.service;

import com.seguros.app.model.Payment;
import com.seguros.app.util.InvoicePdfGenerator;

import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Servicio de envío de correos
    private final JavaMailSender mailSender;

    public EmailService(
            JavaMailSender mailSender
    ) {

        this.mailSender = mailSender;
    }

    // =========================
    // ENVIAR FACTURA PDF
    // =========================

    public void sendInvoiceEmail(
            Payment payment
    ) throws Exception {

        // Email del cliente
        String to =
                payment.getPolicy()
                        .getClient()
                        .getEmail();

        // Crear mensaje
        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true
                );

        // Configuración del correo
        helper.setTo(to);

        helper.setSubject(
                "Insurance Payment Invoice"
        );

        helper.setText(
                """
                Dear client,

                Your payment invoice is attached.

                Thank you for trusting Seguros App.
                """
        );

        // =========================
        // GENERAR PDF EN MEMORIA
        // =========================

        ByteArrayOutputStream pdfStream =
                new ByteArrayOutputStream();

        InvoicePdfGenerator.generate(
                payment,
                pdfStream
        );

        // Adjuntar PDF
        helper.addAttachment(
                "Invoice_" + payment.getId() + ".pdf",
                () -> new java.io.ByteArrayInputStream(
                        pdfStream.toByteArray()
                )
        );

        // Enviar correo
        mailSender.send(message);
    }
}
