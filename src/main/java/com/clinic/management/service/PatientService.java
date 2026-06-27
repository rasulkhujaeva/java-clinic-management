package com.clinic.management.service;

import com.clinic.management.entity.Patient;
import com.clinic.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    // final → bu field constructor'da set edilir, sonra değişmez
    private final PatientRepository patientRepository;

    /*
     * TÜM HASTALARI GETİR
     * Repository'den direkt alıyoruz — iş kuralı yok
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /*
     * ID İLE HASTA BUL
     *
     * orElseThrow → hasta bulunamazsa exception fırlatır
     * RuntimeException yerine ileride custom exception yazabiliriz
     */
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    /*
     * YENİ HASTA KAYDET — iş kuralları burada!
     *
     * Kural 1: Aynı telefon numarasıyla 2 hasta olamaz
     * Kural 2: Aynı email ile 2 hasta olamaz
     */
    public Patient savePatient(Patient patient) {

        // Kural 1: telefon kontrolü
        if (patientRepository.existsByPhone(patient.getPhone())) {
            throw new RuntimeException("A patient with this phone already exists: "
                    + patient.getPhone());
        }

        // Kural 2: email kontrolü (email girilmişse)
        if (patient.getEmail() != null && !patient.getEmail().isBlank()) {
            if (patientRepository.existsByEmail(patient.getEmail())) {
                throw new RuntimeException("A patient with this email already exists: "
                        + patient.getEmail());
            }
        }

        return patientRepository.save(patient);
    }

    /*
     * HASTA GÜNCELLE
     *
     * Adımlar:
     * 1. Mevcut hastayı bul (yoksa hata)
     * 2. Fieldları güncelle
     * 3. Kaydet
     *
     * Neden yeni Patient oluşturmuyoruz?
     * Çünkü id'yi korumamız lazım — aynı satırı güncellemeliyiz
     */
    public Patient updatePatient(Long id, Patient updatedPatient) {

        // Önce mevcut hastayı bul
        Patient existing = getPatientById(id);

        // Fieldları güncelle
        existing.setFullName(updatedPatient.getFullName());
        existing.setDateOfBirth(updatedPatient.getDateOfBirth());
        existing.setGender(updatedPatient.getGender());
        existing.setPhone(updatedPatient.getPhone());
        existing.setEmail(updatedPatient.getEmail());
        existing.setAddress(updatedPatient.getAddress());
        existing.setBloodType(updatedPatient.getBloodType());

        // createdAt değişmez — @Column(updatable=false) ile de koruduk

        return patientRepository.save(existing);
    }

    /*
     * HASTA SİL
     */
    public void deletePatient(Long id) {
        // Önce var mı kontrol et
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }

    /*
     * HASTA ARA — isim, telefon veya email ile
     * search boşsa tüm hastaları döndür
     */
    public List<Patient> searchPatients(String search) {
        if (search == null || search.isBlank()) {
            return getAllPatients();
        }
        return patientRepository.searchPatients(search);
    }

    /*
     * TOPLAM HASTA SAYISI — Dashboard için
     */
    public long getTotalPatients() {
        return patientRepository.count();
    }
}
