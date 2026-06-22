
package com.seguros.app.controller;

import com.seguros.app.model.Insurance;
import com.seguros.app.service.InsuranceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/insurances")
public class InsuranceController {
    
    private final InsuranceService service;
     
    public InsuranceController(InsuranceService service) {
        this.service = service;
        
    }
    
    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {

        if (search != null && !search.isBlank()) {
            model.addAttribute("insurances", service.searchByType(search));
        } else {
            model.addAttribute("insurances", service.listAll());
        }

        model.addAttribute("search", search);

        return "insurances/list";
    }
    
    
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("insurance", new Insurance());
        return "insurances/form";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute Insurance insurance) {
        service.save(insurance);
        return "redirect:/insurances";
    }
    
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("insurance", service.get(id));
        return "insurances/form";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/insurances";
    }    
    
    
}
