package com.leonardo.DSCatalog.DTO;

import com.leonardo.DSCatalog.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO implements Serializable {

    private Long id;
    @Size(min = 5, max = 60, message = "Must be between in five or sixty characters")
    @NotBlank(message = "required field")
    private String name;
    private String description;
    @Positive(message = "Price must be positive")
    private Double price;
    private String imgUrl;
    @PastOrPresent(message = "Product date cannot be in the future")
    private Instant moment;

    private List<CategoryDTO> categories = new ArrayList<>();

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
        categories = entity.getCategories().stream().map(CategoryDTO::new).toList();
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

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
