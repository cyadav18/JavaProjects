package org.guidewire.login.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/home")
public class HomeController {

    @GetMapping
    public Map<String, Object> getHomePage(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to the Home Page!");
        response.put("username", userDetails.getUsername());
        response.put("roles", userDetails.getAuthorities());
        return response;
    }
}
