package com.asusoftware.socialapp.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<AuthenticationResponse> register(
            @RequestPart("request") RegisterRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
      return ResponseEntity.ok(authenticationService.register(request, file));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
      return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

 /*   @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("code") String activationCode) {
        // Verificați dacă codul de activare este valid și corect
        if (isValidActivationCode(activationCode)) {
            // Actualizați starea utilizatorului pentru a-l activa
            authenticationService.activateUser(activationCode);
            return "confirmation-success"; // Afișați o pagină de confirmare cu succes
        } else {
            return "confirmation-failure"; // Afișați o pagină de confirmare cu eroare
        }
    }

    private boolean isValidActivationCode(String activationCode) {
        // Implementați verificarea validității codului de activare
        // Returnați true dacă codul este valid, false în caz contrar
        return true;
    } */
}
