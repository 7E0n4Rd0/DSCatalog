package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.services.validation.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO{

    @NotBlank(message = "required field")
    @Size(min = 8 , message = "must have at least 8 characters")
    private String password;

    public UserInsertDTO(Long id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    public UserInsertDTO(User entity) {
        super(entity);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
