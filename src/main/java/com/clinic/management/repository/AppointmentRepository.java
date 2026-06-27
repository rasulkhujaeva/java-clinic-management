package com.clinic.management.repository;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.Doctor;
import com.clinic.management.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByAppointmentDate(LocalDate date);
    List<Appointment> findByStatus(Appointment.Status status);

    // Bugünün randevuları
    List<Appointment> findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate date);

    /*
     * Çakışma kontrolü — aynı doktor aynı tarih ve saatte
     * başka randevusu var mı?
     */
    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(
            Doctor doctor, LocalDate date, LocalTime time);

    // Dashboard için bugünkü randevu sayısı
    long countByAppointmentDate(LocalDate date);

    // Status'a göre say
    long countByStatus(Appointment.Status status);
}