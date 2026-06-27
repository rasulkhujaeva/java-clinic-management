package com.clinic.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * @Entity  → "Bu class bir veritabanı tablosudur" demek
 *            Hibernate bu class'ı görünce PostgreSQL'de
 *            otomatik olarak 'patients' tablosu oluşturur
 *
 * @Table   → Tablonun adını biz belirliyoruz: "patients"
 *            Yazmasak da olur ama açık yazmak daha temiz
 *
 * @Getter  → Lombok: tüm getter metodları otomatik yazar
 * @Setter  → Lombok: tüm setter metodları otomatik yazar
 *
 * WHY LOMBOK?
 * Normalde her field için şunu yazmak zorunda kalırdın:
 *   public String getName() { return name; }
 *   public void setName(String name) { this.name = name; }
 * 50 field varsa 100 metod yazman lazım. Lombok bunu halleder.
 *
 * @NoArgsConstructor → boş constructor: new Patient()
 * @AllArgsConstructor → tüm fieldlı constructor
 * @Builder → Patient.builder().name("Ali").build() kullanımı
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    /*
     * @Id → Bu field PRIMARY KEY
     * @GeneratedValue → ID otomatik artar: 1, 2, 3, 4...
     * IDENTITY strategy → PostgreSQL'in SERIAL/SEQUENCE'ini kullanır
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    private String fullName;


    @Column(nullable = false)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone is required")
    private String phone;

    /*
     * @Email
     */
    @Email(message = "Enter a valid email")
    @Column(unique = true)
    private String email;

    private String address;

    /*
     * Kan grubu
     */
    @Column(length = 5)
    private String bloodType;

    @Builder.Default
    //@Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /*
     * ENUM — nested class olarak tanımladık
     * Neden enum? Çünkü gender sadece 3 değer alabilir.
     * String kullansaydık "male", "Male", "MALE", "erkek" gibi
     * tutarsız veriler girebilirdi. Enum bunu önler.
     */
    public enum Gender {
        MALE, FEMALE
    }
}