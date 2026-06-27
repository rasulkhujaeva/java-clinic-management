package com.clinic.management.service;

import com.clinic.management.entity.Appointment;
import com.clinic.management.entity.Bill;
import com.clinic.management.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final AppointmentService appointmentService;

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found: " + id));
    }

    public Bill createBill(Long appointmentId, Bill bill) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (billRepository.existsByAppointment(appointment)) {
            throw new RuntimeException("A bill already exists for this appointment!");
        }

        bill.setAppointment(appointment);
        bill.calculateTotal(); // toplam otomatik hesapla
        return billRepository.save(bill);
    }

    public Bill updateBill(Long id, Bill updated) {
        Bill existing = getBillById(id);
        existing.setConsultationFee(updated.getConsultationFee());
        existing.setMedicationFee(updated.getMedicationFee());
        existing.setLabFee(updated.getLabFee());
        existing.setOtherFee(updated.getOtherFee());
        existing.setNotes(updated.getNotes());
        existing.setPaymentMethod(updated.getPaymentMethod());
        existing.calculateTotal();
        return billRepository.save(existing);
    }

    public Bill markAsPaid(Long id, Bill.PaymentMethod method) {
        Bill bill = getBillById(id);
        bill.setPaymentStatus(Bill.PaymentStatus.PAID);
        bill.setPaymentMethod(method);
        bill.setPaidAt(LocalDateTime.now());
        return billRepository.save(bill);
    }

    public BigDecimal getTotalRevenue() {
        return billRepository.getTotalRevenue();
    }

    public long getPendingCount() {
        return billRepository.countByPaymentStatus(Bill.PaymentStatus.PENDING);
    }
}