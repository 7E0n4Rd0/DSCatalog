package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.EmailDTO;
import com.leonardo.DSCatalog.entities.PasswordRecover;
import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.repositories.PasswordRecoverRepository;
import com.leonardo.DSCatalog.repositories.UserRepository;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;


    @Transactional
    public void createRecoverToken(EmailDTO body) {
        String token = UUID.randomUUID().toString();
        String text = "Enter on the link to create a new password\n\n" + recoverUri + token + "\n" +
                "Validade de " + tokenMinutes + "minutos.";
        Optional<User> user = userRepository.findByEmail(body.getEmail());

        if (user.isEmpty()){
            throw new ResourceNotFoundException("Email not found");
        }

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        entity = passwordRecoverRepository.save(entity);

        emailService.sendMail(body.getEmail(), "Password Recover", text);
    }
}
