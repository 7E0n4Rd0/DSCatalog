package com.leonardo.DSCatalog.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class EmailDTO implements Serializable {

    @Email(message = "Invalid Email")
    @NotBlank(message = "required field")
    private String email;

    public EmailDTO(){}

    public EmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
