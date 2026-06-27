package com.clinic.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * @ManyToOne → Çok randevu bir hastaya ait olabilir
     * @JoinColumn → appointments tablosunda patient_id kolonu oluşur
     * Bu kolon patients.id'ye foreign key olur
     */
// LAZY → EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate appointmentDate;

    @Column(nullable = false)
    @NotNull(message = "Time is required")
    private LocalTime appointmentTime;

    /*
     * Randevu durumu — enum kullanıyoruz
     * Sadece bu 4 değer olabilir
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.SCHEDULED;

    private String notes;

    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        SCHEDULED,   // Planlandı
        COMPLETED,   // Tamamlandı
        CANCELLED,   // İptal edildi
        NO_SHOW      // Hasta gelmedi
    }
}