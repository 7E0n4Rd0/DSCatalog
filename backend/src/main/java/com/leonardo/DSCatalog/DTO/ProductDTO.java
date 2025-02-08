package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.Category;
import com.leonardo.DSCatalog.entities.Product;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class ProductDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;
    private Instant moment;

    private Set<CategoryDTO> categories = new HashSet<>();

    public ProductDTO(Long id, String name, String description, Double price, String imgUrl, Instant moment) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.moment = moment;
    }

    public ProductDTO(Product entity){
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        moment = entity.getMoment();
    }

    public ProductDTO(Product entity, Set<Category> categories){
        this(entity);
        categories.forEach(cat -> this.categories.add(new CategoryDTO(cat)));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Instant getMoment() {
        return moment;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }
}
