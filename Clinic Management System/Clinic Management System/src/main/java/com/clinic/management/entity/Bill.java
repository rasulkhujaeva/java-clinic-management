package com.clinic.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Bill = Fatura
 * Her appointment'ın bir faturası olabilir.
 * @OneToOne → bir randevu = bir fatura
 */
@Entity
@Table(name = "bills")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * @OneToOne → Bir randevunun bir faturası var
     * @JoinColumn → bills tablosunda appointment_id kolonu
     */
    // LAZY → EAGER
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /*
     * BigDecimal → para için her zaman BigDecimal kullan!
     * double/float kullanma — yuvarlama hatası yapar
     * 0.1 + 0.2 = 0.30000000000000004 (double)
     * 0.1 + 0.2 = 0.3 (BigDecimal)
     */
    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal medicationFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal labFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal otherFee = BigDecimal.ZERO;

    // Toplam = consultation + medication + lab + other
    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String notes;

    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

    public enum PaymentStatus {
        PENDING,   // Ödeme bekleniyor
        PAID,      // Ödendi
        CANCELLED  // İptal edildi
    }

    public enum PaymentMethod {
        CASH, CREDIT_CARD, INSURANCE, BANK_TRANSFER
    }

    // Toplam otomatik hesapla
    public void calculateTotal() {
        this.totalAmount = consultationFee
                .add(medicationFee)
                .add(labFee)
                .add(otherFee);
    }
}