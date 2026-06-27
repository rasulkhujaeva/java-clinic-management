package com.clinic.management.controller;

import com.clinic.management.entity.Doctor;
import com.clinic.management.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public String listDoctors(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("doctors", doctorService.searchDoctors(search));
        model.addAttribute("search", search);
        return "doctors/list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctors/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        return "doctors/form";
    }

    @PostMapping("/save")
    public String saveDoctor(
            @Valid @ModelAttribute("doctor") Doctor doctor,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) return "doctors/form";

        try {
            if (doctor.getId() == null) {
                doctorService.saveDoctor(doctor);
                redirectAttributes.addFlashAttribute("successMessage", "Doctor added successfully!");
            } else {
                doctorService.updateDoctor(doctor.getId(), doctor);
                redirectAttributes.addFlashAttribute("successMessage", "Doctor updated successfully!");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "doctors/form";
        }

        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Doctor deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete: " + e.getMessage());
        }
        return "redirect:/doctors";
    }

    @GetMapping("/toggle/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.getDoctorById(id);
        doctor.setAvailable(!doctor.getAvailable());
        doctorService.updateDoctor(id, doctor);
        redirectAttributes.addFlashAttribute("successMessage",
                doctor.getFullName() + " is now " + (doctor.getAvailable() ? "available" : "unavailable"));
        return "redirect:/doctors";
    }
}

