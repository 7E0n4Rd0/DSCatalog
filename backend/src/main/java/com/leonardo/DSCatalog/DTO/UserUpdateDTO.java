package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.services.validation.UserInsertValid;
import com.leonardo.DSCatalog.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO{

    public UserUpdateDTO(Long id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    public UserUpdateDTO(User entity) {
        super(entity);
    }
}
