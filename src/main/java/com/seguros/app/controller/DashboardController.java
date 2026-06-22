package com.seguros.app.controller;

import com.seguros.app.service.ClientService;
import com.seguros.app.service.InsuranceService;
import com.seguros.app.service.PaymentService;
import com.seguros.app.service.PolicyService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // =========================
    // SERVICES
    // =========================

    private final ClientService clientService;

    private final InsuranceService insuranceService;

    private final PolicyService policyService;

    private final PaymentService paymentService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public DashboardController(

            ClientService clientService,

            InsuranceService insuranceService,

            PolicyService policyService,

            PaymentService paymentService

    ) {

        this.clientService = clientService;

        this.insuranceService = insuranceService;

        this.policyService = policyService;

        this.paymentService = paymentService;
    }

    // =========================
    // DASHBOARD
    // =========================

    @GetMapping({"/", "/dashboard"})

    public String dashboard(Model model) {

        // =========================
        // GENERAL METRICS
        // =========================

        // Total clients
        model.addAttribute(
                "totalClients",
                clientService.listAll().size()
        );

        // Total insurances
        model.addAttribute(
                "totalInsurances",
                insuranceService.listAll().size()
        );

        // Total policies
        model.addAttribute(
                "totalPolicies",
                policyService.listAll().size()
        );

        // Total payments
        model.addAttribute(
                "totalPayments",
                paymentService.listAll().size()
        );

        // Total revenue
        model.addAttribute(
                "totalRevenue",
                paymentService.totalRevenue()
        );

        // =========================
        // POLICY STATES
        // =========================

        // Active policies
        long activePolicies =
                policyService.listAll()
                        .stream()
                        .filter(policy ->
                                policy.getState() != null
                                &&
                                policy.getState()
                                        .equalsIgnoreCase(
                                                "ACTIVE"
                                        )
                        )
                        .count();

        // Pending policies
        long pendingPolicies =
                policyService.listAll()
                        .stream()
                        .filter(policy ->
                                policy.getState() != null
                                &&
                                policy.getState()
                                        .equalsIgnoreCase(
                                                "PENDING"
                                        )
                        )
                        .count();

        // Cancelled policies
        long cancelledPolicies =
                policyService.listAll()
                        .stream()
                        .filter(policy ->
                                policy.getState() != null
                                &&
                                policy.getState()
                                        .equalsIgnoreCase(
                                                "CANCELLED"
                                        )
                        )
                        .count();

        // Send metrics to dashboard
        model.addAttribute(
                "activePolicies",
                activePolicies
        );

        model.addAttribute(
                "pendingPolicies",
                pendingPolicies
        );

        model.addAttribute(
                "cancelledPolicies",
                cancelledPolicies
        );

        // =========================
        // CHARTS
        // =========================

        // Most sold insurances
        model.addAttribute(
                "insuranceSales",
                policyService.countPoliciesByInsurance()
        );

        // Policy states
        model.addAttribute(
                "policyStates",
                policyService.countPoliciesByState()
        );

        // Payments by method
        model.addAttribute(
                "paymentsByMethod",
                paymentService.paymentsByMethod()
        );

        // =========================
        // AUTHENTICATED USER
        // =========================

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // Username
        String username = auth.getName();

        // Role
        String role = auth.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        // Send user data
        model.addAttribute(
                "loggedUsername",
                username
        );

        model.addAttribute(
                "loggedRole",
                role
        );

        // =========================
        // RETURN VIEW
        // =========================

        return "dashboard";
    }
}