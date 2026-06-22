
package com.seguros.app.controller;

import com.seguros.app.model.Policy;
import com.seguros.app.model.Client;
import com.seguros.app.service.ClientService;
import com.seguros.app.service.InsuranceService;
import com.seguros.app.service.PolicyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/policies")
public class PolicyController {
    
    private final PolicyService policyService;
    private final ClientService clientService;
    private final InsuranceService insuranceService;

    public PolicyController(PolicyService policyService,
                            ClientService clientService,
                            InsuranceService insuranceService) {
        this.policyService = policyService;
        this.clientService = clientService;
        this.insuranceService = insuranceService;
    }
    
    //Metodo qe atiende las peticiones Get a /policies
    //Permite listar todas las polizas o filtrarlas por su estado

    // LISTAR PÓLIZAS
    @GetMapping
    public String list(
            @RequestParam(required = false) String state,
            Model model,
            Authentication auth
    ) {

        // Obtiene el username del usuario autenticado
        String username = auth.getName();

        // Verifica si el usuario es ADMIN o EMPLOYEE
        boolean isAdminOrEmployee = auth.getAuthorities()
                .stream()
                .anyMatch(authority ->

                        authority.getAuthority()
                                .equals("ROLE_ADMIN")

                        ||

                        authority.getAuthority()
                                .equals("ROLE_EMPLOYEE")
                );

        // =========================
        // ADMIN Y EMPLOYEE
        // =========================

        if (isAdminOrEmployee) {

            // Filtrar por estado
            if (state != null && !state.isBlank()) {

                model.addAttribute(
                        "policies",
                        policyService.searchByState(state)
                );

            } else {

                // Mostrar todas las pólizas
                model.addAttribute(
                        "policies",
                        policyService.listAll()
                );
            }

        }

        // =========================
        // CLIENT
        // =========================

        else {

            // Busca el cliente asociado al usuario
            Client client =
                    clientService.findByUsername(username);

            // Mostrar SOLO las pólizas del cliente
            model.addAttribute(
                    "policies",
                    policyService.findByClient(client)
            );
        }

        // Envía el filtro actual a la vista
        model.addAttribute("state", state);

        // Retorna la vista
        return "policies/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("policy", new Policy());
        model.addAttribute("clients", clientService.listAll());
        model.addAttribute("insurances", insuranceService.listAll());
        return "policies/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Long client,
                   @RequestParam Long insurance,
                   @ModelAttribute Policy policy) {

        policy.setClient(clientService.get(client));
        policy.setInsurance(insuranceService.get(insurance));

        policyService.save(policy);

        return "redirect:/policies";
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "policy",
                policyService.get(id)
        );

        model.addAttribute(
                "clients",
                clientService.listAll()
        );

        model.addAttribute(
                "insurances",
                insuranceService.listAll()
        );

        return "policies/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        policyService.delete(id);
        return "redirect:/policies";
    }
    
}
