package com.clinic.management.service;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;
import com.clinic.management.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getTodayAppointments() {
        return appointmentRepository
                .findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate.now());
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
    }

    public Appointment saveAppointment(Long patientId, Long doctorId, Appointment appointment) {
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctorById(doctorId);

        /*
         * İŞ KURALI: Aynı doktor aynı saat ve tarihte
         * 2 randevu alamaz — çakışma kontrolü
         */
        if (appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTime(
                doctor, appointment.getAppointmentDate(), appointment.getAppointmentTime())) {
            throw new RuntimeException(
                    "Doctor " + doctor.getFullName() +
                            " already has an appointment at " +
                            appointment.getAppointmentTime() + " on " +
                            appointment.getAppointmentDate());
        }

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateStatus(Long id, Appointment.Status status) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateNotes(Long id, String notes) {
        Appointment appointment = getAppointmentById(id);
        appointment.setNotes(notes);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.delete(getAppointmentById(id));
    }

    public long getTodayCount() {
        return appointmentRepository.countByAppointmentDate(LocalDate.now());
    }

    public long getScheduledCount() {
        return appointmentRepository.countByStatus(Appointment.Status.SCHEDULED);
    }
}