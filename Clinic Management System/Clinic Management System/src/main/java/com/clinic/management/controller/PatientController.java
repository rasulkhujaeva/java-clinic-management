package com.clinic.management.controller;

import com.clinic.management.entity.Patient;
import com.clinic.management.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * @Controller → Bu class HTTP isteklerini karşılar
 *               Thymeleaf HTML sayfaları döndürür
 *               (REST API için @RestController kullanılır)
 *
 * @RequestMapping("/patients") → Bu class'taki tüm URL'ler
 *                                 /patients ile başlar
 */
@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /*
     * GET /patients
     * Tüm hastaları listele — ana hasta sayfası
     *
     * @RequestParam(required=false) → search parametresi
     *   olmayabilir: /patients
     *   olabilir:    /patients?search=ali
     *
     * Model → HTML sayfasına veri göndermek için
     *   model.addAttribute("patients", liste)
     *   HTML'de: ${patients} ile erişirsin
     */
    @GetMapping
    public String listPatients(
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("patients", patientService.searchPatients(search));
        model.addAttribute("search", search);
        return "patients/list";
        // Bu → src/main/resources/templates/patients/list.html döndürür
    }

    /*
     * GET /patients/new
     * Boş form aç — yeni hasta eklemek için
     *
     * model.addAttribute("patient", new Patient())
     * → HTML form'una boş bir Patient bağlıyoruz
     *   Form submit edilince bu obje dolacak
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("genders", Patient.Gender.values());
        return "patients/form";
    }

    /*
     * GET /patients/edit/{id}
     * Mevcut hastanın bilgilerini forma doldur
     *
     * @PathVariable → URL'deki {id} buraya gelir
     *   /patients/edit/5 → id = 5
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        model.addAttribute("genders", Patient.Gender.values());
        return "patients/form";
        // Aynı form — ama bu sefer dolu gelecek
    }

    /*
     * POST /patients/save
     * Formu kaydet — hem yeni hem güncelleme için
     *
     * @Valid → Patient üzerindeki @NotBlank @Email gibi
     *          validasyonları çalıştır
     *
     * BindingResult → validasyon hataları buraya düşer
     *   ÖNEMLI: @Valid'den hemen sonra gelmeli!
     *
     * RedirectAttributes → redirect sonrası mesaj göstermek için
     *   "flashAttr" → bir kez gösterilir, sonra silinir
     */
    @PostMapping("/save")
    public String savePatient(
            @Valid @ModelAttribute("patient") Patient patient,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validasyon hatası varsa formu tekrar göster
        if (result.hasErrors()) {
            model.addAttribute("genders", Patient.Gender.values());
            return "patients/form";
        }

        try {
            if (patient.getId() == null) {
                // id yoksa → yeni hasta
                patientService.savePatient(patient);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Patient added successfully!");
            } else {
                // id varsa → güncelleme
                patientService.updatePatient(patient.getId(), patient);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Patient updated successfully!");
            }
        } catch (RuntimeException e) {
            // Service'den gelen hata (telefon zaten var gibi)
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("genders", Patient.Gender.values());
            return "patients/form";
        }

        return "redirect:/patients";
        // redirect → tarayıcıyı /patients'a yönlendir
        // Böylece sayfayı yenilince form tekrar submit olmaz
    }

    /*
     * GET /patients/delete/{id}
     * Hastayı sil
     *
     * Neden POST değil GET?
     * Thymeleaf ile link olarak kullanmak kolay.
     * Production'da DELETE metodu kullanılır ama
     * şimdilik GET yeterli.
     */
    @GetMapping("/delete/{id}")
    public String deletePatient(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            patientService.deletePatient(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete patient: " + e.getMessage());
        }

        return "redirect:/patients";
    }
}