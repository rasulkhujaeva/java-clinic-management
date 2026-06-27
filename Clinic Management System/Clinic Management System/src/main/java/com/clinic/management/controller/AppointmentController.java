package com.clinic.management.controller;

import com.clinic.management.entity.Appointment;
import com.clinic.management.service.AppointmentService;
import com.clinic.management.service.DoctorService;
import com.clinic.management.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("statuses", Appointment.Status.values());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAvailableDoctors());
        model.addAttribute("statuses", Appointment.Status.values());
        return "appointments/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("statuses", Appointment.Status.values());
        return "appointments/form";
    }

    @PostMapping("/save")
    public String saveAppointment(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @ModelAttribute("appointment") Appointment appointment,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            if (appointment.getId() == null) {
                appointmentService.saveAppointment(patientId, doctorId, appointment);
                redirectAttributes.addFlashAttribute("successMessage", "Appointment booked!");
            } else {
                appointmentService.saveAppointment(patientId, doctorId, appointment);
                redirectAttributes.addFlashAttribute("successMessage", "Appointment updated!");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
            model.addAttribute("statuses", Appointment.Status.values());
            return "appointments/form";
        }

        return "redirect:/appointments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment deleted!");
        return "redirect:/appointments";
    }

    @GetMapping("/status/{id}/{status}")
    public String updateStatus(@PathVariable Long id,
                               @PathVariable Appointment.Status status,
                               RedirectAttributes redirectAttributes) {
        appointmentService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Status updated to " + status);
        return "redirect:/appointments";
    }
}