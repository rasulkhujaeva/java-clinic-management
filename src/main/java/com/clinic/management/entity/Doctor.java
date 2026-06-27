package com.clinic.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    private String fullName;

    /*
     * Uzmanlık alanı — Cardiology, Dentistry, General Practice vs.
     * Enum yerine String kullandık çünkü
     * hastaneden hastaneye farklı uzmanlıklar olabilir.
     */
    @Column(nullable = false)
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @Column(nullable = false)
    @NotBlank(message = "Phone is required")
    private String phone;

    @Email(message = "Enter a valid email")
    @Column(unique = true)
    private String email;

    /*
     * Doktor müsait mi?
     * true  → randevu alınabilir
     * false → tatilde, izinde vs.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean available = true;

    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}