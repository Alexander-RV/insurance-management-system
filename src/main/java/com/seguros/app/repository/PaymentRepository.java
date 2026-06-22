package com.seguros.app.repository;

import com.seguros.app.model.Client;
import com.seguros.app.model.Payment;

import java.time.LocalDate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    // =========================
    // SEARCH BY DATE
    // =========================

    List<Payment> findByPaymentDate(
            LocalDate paymentDate
    );

    // =========================
    // SEARCH PAYMENTS BY CLIENT
    // =========================

    List<Payment> findByPolicyClient(
            Client client
    );

    // =========================
    // PAYMENTS BY METHOD
    // =========================

    @Query("""
           SELECT p.paymentMethod,
                  COUNT(p)
           FROM Payment p
           GROUP BY p.paymentMethod
           """)
    List<Object[]> paymentsByMethod();
}