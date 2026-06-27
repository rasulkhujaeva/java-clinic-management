package com.clinic.management.repository;

import com.clinic.management.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByFullNameContainingIgnoreCase(String name);


    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);


    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "p.phone LIKE CONCAT('%', :search, '%') OR " +
            "p.email LIKE CONCAT('%', :search, '%')")
    List<Patient> searchPatients(@Param("search") String search);
}