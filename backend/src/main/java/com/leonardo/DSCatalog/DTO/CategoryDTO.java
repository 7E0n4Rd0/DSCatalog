package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.Category;

import java.io.Serializable;

public class CategoryDTO implements Serializable {
    private Long id;
    private String name;

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(Category entity){
        id = entity.getId();
        name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
