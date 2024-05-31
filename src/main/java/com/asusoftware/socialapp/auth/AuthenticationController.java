package com.asusoftware.socialapp.auth;

import com.asusoftware.socialapp.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final LogoutHandler logoutHandler;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping(value = "/register")
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            boolean isValid = jwtService.isTokenExpired(actualToken);
            return ResponseEntity.ok(isValid);
        }
        return ResponseEntity.badRequest().body("Invalid token format");
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
