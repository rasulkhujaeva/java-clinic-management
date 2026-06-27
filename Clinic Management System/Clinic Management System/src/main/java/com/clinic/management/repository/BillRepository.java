package com.clinic.management.repository;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointment(Appointment appointment);
    boolean existsByAppointment(Appointment appointment);
    List<Bill> findByPaymentStatus(Bill.PaymentStatus status);

    // Dashboard — toplam gelir
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();

    long countByPaymentStatus(Bill.PaymentStatus status);
}