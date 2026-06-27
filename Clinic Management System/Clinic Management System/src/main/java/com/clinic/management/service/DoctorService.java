package com.clinic.management.service;

import com.clinic.management.entity.Doctor;
import com.clinic.management.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
    }

    public Doctor saveDoctor(Doctor doctor) {
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            throw new RuntimeException("License number already exists: " + doctor.getLicenseNumber());
        }
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor updated) {
        Doctor existing = getDoctorById(id);
        existing.setFullName(updated.getFullName());
        existing.setSpecialization(updated.getSpecialization());
        existing.setLicenseNumber(updated.getLicenseNumber());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setAvailable(updated.getAvailable());
        return doctorRepository.save(existing);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.delete(getDoctorById(id));
    }

    public List<Doctor> searchDoctors(String search) {
        if (search == null || search.isBlank()) return getAllDoctors();
        return doctorRepository.findByFullNameContainingIgnoreCase(search);
    }

    public long getTotalDoctors() {
        return doctorRepository.count();
    }
}
