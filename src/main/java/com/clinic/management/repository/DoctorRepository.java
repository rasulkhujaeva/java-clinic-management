package com.clinic.management.repository;

import com.clinic.management.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Müsait doktorları getir — randevu alırken kullanılır
    List<Doctor> findByAvailableTrue();

    // İsme göre ara
    List<Doctor> findByFullNameContainingIgnoreCase(String name);

    // Uzmanlığa göre filtrele
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByEmail(String email);
}
