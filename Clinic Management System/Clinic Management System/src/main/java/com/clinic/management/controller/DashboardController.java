package com.clinic.management.controller;

import com.clinic.management.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final BillService billService;

    // Ana sayfa → dashboard'a yönlendir
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // İstatistik kartları için sayılar
        model.addAttribute("totalPatients",     patientService.getTotalPatients());
        model.addAttribute("totalDoctors",      doctorService.getTotalDoctors());
        model.addAttribute("todayAppointments", appointmentService.getTodayCount());
        model.addAttribute("pendingBills",      billService.getPendingCount());
        model.addAttribute("totalRevenue",      billService.getTotalRevenue());

        // Bugünün randevuları — tabloda göster
        model.addAttribute("todayList", appointmentService.getTodayAppointments());

        return "dashboard";
    }
}