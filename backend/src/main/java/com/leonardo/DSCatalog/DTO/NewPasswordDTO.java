package com.leonardo.DSCatalog.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class NewPasswordDTO implements Serializable {

    @NotBlank(message = "required field")
    private String token;
    @NotBlank(message = "required field")
    @Size(min = 8, message = "must have at least 8 characters")
    private String password;

    public NewPasswordDTO(){}

    public NewPasswordDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
