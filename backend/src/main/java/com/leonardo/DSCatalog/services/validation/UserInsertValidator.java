package com.leonardo.DSCatalog.services.validation;

import com.leonardo.DSCatalog.entities.User;
import com.leonardo.DSCatalog.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.leonardo.DSCatalog.DTO.UserInsertDTO;
import com.leonardo.DSCatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository repository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        User user = repository.findByEmail(dto.getEmail());

        if(user != null){
            list.add(new FieldMessage("email", "Email exists"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}