package com.clinic.management.controller;

import com.clinic.management.entity.Bill;
import com.clinic.management.service.AppointmentService;
import com.clinic.management.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;
    private final AppointmentService appointmentService;

    @GetMapping
    public String listBills(Model model) {
        model.addAttribute("bills", billService.getAllBills());
        model.addAttribute("totalRevenue", billService.getTotalRevenue());
        model.addAttribute("pendingCount", billService.getPendingCount());
        return "billing/list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("paymentMethods", Bill.PaymentMethod.values());
        return "billing/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("bill", billService.getBillById(id));
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("paymentMethods", Bill.PaymentMethod.values());
        return "billing/form";
    }

    @PostMapping("/save")
    public String saveBill(
            @RequestParam Long appointmentId,
            @ModelAttribute("bill") Bill bill,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            if (bill.getId() == null) {
                billService.createBill(appointmentId, bill);
                redirectAttributes.addFlashAttribute("successMessage", "Bill created!");
            } else {
                billService.updateBill(bill.getId(), bill);
                redirectAttributes.addFlashAttribute("successMessage", "Bill updated!");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointments", appointmentService.getAllAppointments());
            model.addAttribute("paymentMethods", Bill.PaymentMethod.values());
            return "billing/form";
        }
        return "redirect:/billing";
    }

    @GetMapping("/pay/{id}")
    public String showPayForm(@PathVariable Long id, Model model) {
        model.addAttribute("bill", billService.getBillById(id));
        model.addAttribute("paymentMethods", Bill.PaymentMethod.values());
        return "billing/pay";
    }

    @PostMapping("/pay/{id}")
    public String processPay(@PathVariable Long id,
                             @RequestParam Bill.PaymentMethod paymentMethod,
                             RedirectAttributes redirectAttributes) {
        billService.markAsPaid(id, paymentMethod);
        redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully!");
        return "redirect:/billing";
    }
}
