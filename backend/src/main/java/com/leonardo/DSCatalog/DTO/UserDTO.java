package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserDTO(User entity){
        id = entity.getId();;
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        roles.addAll(entity.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet()));
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }
}
