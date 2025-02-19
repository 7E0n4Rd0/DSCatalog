package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.Role;

import java.io.Serializable;

public class RoleDTO implements Serializable {

    private Long id;
    private String authority;

    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role entity){
        id = entity.getId();
        authority = entity.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }
}
