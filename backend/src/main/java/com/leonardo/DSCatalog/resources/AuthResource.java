package com.leonardo.DSCatalog.resources;

import com.leonardo.DSCatalog.DTO.EmailDTO;
import com.leonardo.DSCatalog.DTO.NewPasswordDTO;
import com.leonardo.DSCatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO body){
        authService.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto){
        authService.saveNewPassword(dto);

        return ResponseEntity.noContent().build();
    }
}
