package com.seguros.app.service;

import com.seguros.app.model.Client;
import com.seguros.app.model.Payment;

import com.seguros.app.repository.PaymentRepository;

import java.time.LocalDate;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // =========================
    // REPOSITORY
    // =========================

    private final PaymentRepository repo;

    // =========================
    // CONSTRUCTOR
    // =========================

    public PaymentService(
            PaymentRepository repo
    ) {

        this.repo = repo;
    }

    // =========================
    // LIST ALL PAYMENTS
    // =========================

    public List<Payment> listAll() {

        return repo.findAll();
    }

    // =========================
    // SAVE PAYMENT
    // =========================

    public Payment save(
            Payment p
    ) {

        return repo.save(p);
    }

    // =========================
    // SEARCH BY PAYMENT DATE
    // =========================

    public List<Payment> searchByPaymentDate(
            LocalDate paymentDate
    ) {

        return repo.findByPaymentDate(
                paymentDate
        );
    }

    // =========================
    // GET PAYMENT BY ID
    // =========================

    public Payment get(
            Long id
    ) {

        return repo.findById(id)
                .orElse(null);
    }

    // =========================
    // FIND PAYMENTS BY CLIENT
    // =========================

    public List<Payment> findByClient(
            Client client
    ) {

        return repo.findByPolicyClient(
                client
        );
    }

    // =========================
    // TOTAL REVENUE
    // =========================

    public double totalRevenue() {

        return repo.findAll()
                .stream()
                .mapToDouble(payment ->

                        payment.getAmount() != null

                        ? payment.getAmount()

                        : 0.0

                )
                .sum();
    }

    // =========================
    // PAYMENTS BY METHOD
    // =========================

    public List<Object[]> paymentsByMethod() {

        return repo.paymentsByMethod();
    }
}