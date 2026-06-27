package com.clinic.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /*
     * GET /login → login.html sayfasını aç
     * POST /login → Spring Security otomatik halleder
     *               Biz POST controller yazmıyoruz!
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}