package com.seguros.app.controller;

import com.seguros.app.model.Payment;
import com.seguros.app.model.Client;
import com.seguros.app.service.EmailService;
import com.seguros.app.service.PaymentService;
import com.seguros.app.service.PolicyService;
import com.seguros.app.service.ClientService;
import com.seguros.app.util.InvoicePdfGenerator;
import com.seguros.app.util.PaymentReportPdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PolicyService policyService;
    // Servicio de clientes
    private final ClientService clientService;
    
    //Servicios de correos
    private final EmailService emailService;

    public PaymentController(
            PaymentService paymentService,
            PolicyService policyService,
            ClientService clientService,
            EmailService emailService
    ) {

        this.paymentService = paymentService;
        this.policyService = policyService;
        this.clientService = clientService;
        this.emailService = emailService;
    }

    //Metodo para listar los pagos o filtrar por fecha
        // =========================
     // LISTAR PAGOS
     // =========================

     @GetMapping
     public String list(
             @RequestParam(required = false)
             LocalDate payment,

             Model model,

             Authentication auth
     ) {

         // Obtiene username del usuario autenticado
         String username = auth.getName();

         // Verifica si es ADMIN o EMPLOYEE
         boolean isAdminOrEmployee =
                 auth.getAuthorities()
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

             // Filtrar por fecha
             if (payment != null) {

                 model.addAttribute(
                         "payments",
                         paymentService.searchByPaymentDate(payment)
                 );

             } else {

                 // Mostrar todos los pagos
                 model.addAttribute(
                         "payments",
                         paymentService.listAll()
                 );
             }

         }

         // =========================
         // CLIENT
         // =========================

         else {

             // Buscar cliente asociado
             Client client =
                     clientService.findByUsername(username);

             // Mostrar SOLO pagos de sus pólizas
             model.addAttribute(
                     "payments",
                     paymentService.findByClient(client)
             );
         }

         return "payments/list";
     }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("payment", new Payment());
        model.addAttribute("policies", policyService.listAll());
        return "payments/form";
    }

     
    @PostMapping("/save")
    public String save(
            @RequestParam Long policy,
            @ModelAttribute Payment payment
    ) throws Exception {
        
        //Asocia la poliza al pago
        
        payment.setPolicy(
                policyService.get(policy)
        );
        
        //Guarda el pago
        Payment savedPayment =
                paymentService.save(payment);
        
        //Envia factura al correo
        emailService.sendInvoiceEmail(
                savedPayment
        );

        return "redirect:/payments";
    }
    
    // Generates and downloads the invoice PDF for a payment
    // =========================
    // DESCARGAR FACTURA PDF
    // =========================

    @GetMapping("/invoice/{id}")
    public void generateInvoice(

            @PathVariable Long id,

            HttpServletResponse response,

            Authentication auth

    ) throws Exception {

        // Busca el pago
        Payment payment = paymentService.get(id);

        // Si no existe
        if (payment == null) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );

            return;
        }

        // =========================
        // VALIDAR CLIENTE
        // =========================

        boolean isClient =
                auth.getAuthorities()
                .stream()
                .anyMatch(authority ->

                        authority.getAuthority()
                                .equals("ROLE_CLIENT")
                );

        // Si es cliente
        if (isClient) {

            // Username autenticado
            String username = auth.getName();

            // Cliente asociado
            Client client =
                    clientService.findByUsername(
                            username
                    );

            // Validar que el pago pertenezca al cliente
            if (!payment.getPolicy()
                    .getClient()
                    .getId()
                    .equals(client.getId())) {

                throw new AccessDeniedException(
                        "Access denied"
                );
            }
        }

        // =========================
        // GENERAR PDF
        // =========================

        response.setContentType(
                "application/pdf"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Invoice_" + id + ".pdf"
        );

        OutputStream out =
                response.getOutputStream();

        InvoicePdfGenerator.generate(
                payment,
                out
        );

        out.flush();
        out.close();
    }
    
    @GetMapping("/report")
    public void generatePaymentsReport(HttpServletResponse response) throws Exception {

        // Configura el tipo de contenido como PDF
        response.setContentType("application/pdf");

        // Define el nombre del archivo que se descargará
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=payments-report.pdf"
        );

        // Obtiene todos los pagos registrados
        List<Payment> payments = paymentService.listAll();

        // Genera el reporte PDF y lo envía al navegador
        PaymentReportPdfGenerator.generate(
                payments,
                response.getOutputStream()
        );
    }
    
    // Enviar factura por email
    @GetMapping("/send-email/{id}")
    
    public String sendInvoiceEmail(
            @PathVariable Long id,
            
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) throws Exception {
        
        // Busca el pago
        Payment payment =
                paymentService.get(id);
        
        //Enviar la factura 
        emailService.sendInvoiceEmail(
                payment
        );
        
        //Mensaje de correo exitoso
        redirectAttributes.addFlashAttribute(
                "success",
                "Invoice sent sucessfully"
        );
        
        return "redirect:/payments";
        
    }   
}
