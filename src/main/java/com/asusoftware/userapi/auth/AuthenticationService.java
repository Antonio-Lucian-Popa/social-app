package com.asusoftware.userapi.auth;

import com.asusoftware.userapi.config.JwtService;
import com.asusoftware.userapi.email.services.EmailService;
import com.asusoftware.userapi.user.model.Role;
import com.asusoftware.userapi.user.model.User;
import com.asusoftware.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;


    /**
     * @param request
     * @return
     */
    public AuthenticationResponse register(RegisterRequest request) throws Exception {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // Generați un cod de activare unic și construiți URL-ul de confirmare
//        String activationCode = generateActivationCode();
        //String confirmationLink = "https://your-app.com/confirm?code=" + activationCode;
//        String confirmationLink = "http://localhost:8080/confirm?code=" + activationCode;

        // Trimiteți e-mailul de confirmare
//        emailService.sendConfirmationEmail(user.getEmail(), confirmationLink);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String generateActivationCode() {
        // Implementați generarea codului de activare unic
        // Returnați codul generat
        return UUID.randomUUID().toString();
    }

    public void activateUser(String activationCode) {
        // Implementați actualizarea stării utilizatorului pentru a-l activa
        User user = userRepository.findByActivationCode(activationCode).orElseThrow();
        // Actualizați starea utilizatorului pentru a-l activa
        user.setEnabled(true);
        // userRepository.updateUserStatus(activationCode);
        userRepository.save(user);
    }
}
