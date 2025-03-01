package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.EmailDTO;
import com.leonardo.DSCatalog.DTO.NewPasswordDTO;
import com.leonardo.DSCatalog.entities.PasswordRecover;
import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.repositories.PasswordRecoverRepository;
import com.leonardo.DSCatalog.repositories.UserRepository;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;


    @Transactional
    public void createRecoverToken(EmailDTO body) {
        Optional<User> user = userRepository.findByEmail(body.getEmail());
        if (user.isEmpty()){
            throw new ResourceNotFoundException("Email not found");
        }

        String token = UUID.randomUUID().toString();
        String text = "Enter on the link to create a new password\n\n" + recoverUri + token + "\n" +
                "Validade de " + tokenMinutes + "minutos.";

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        entity = passwordRecoverRepository.save(entity);

        emailService.sendMail(body.getEmail(), "Password Recover", text);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(dto.getToken(), Instant.now());
        if (result.isEmpty()){
            throw new ResourceNotFoundException("Invalid Token");
        }

        User user = userRepository.findByEmail(result.getFirst().getEmail()).get();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
    }

    protected Optional<User> authenticated() {
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }
}
