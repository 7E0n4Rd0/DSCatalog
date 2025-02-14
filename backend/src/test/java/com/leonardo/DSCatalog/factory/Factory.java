package com.leonardo.DSCatalog.factory;

import com.leonardo.DSCatalog.DTO.CategoryDTO;
import com.leonardo.DSCatalog.DTO.ProductDTO;
import com.leonardo.DSCatalog.entities.Category;
import com.leonardo.DSCatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct(){
        Product product = new Product(1L, "Phone", "Good Phone", 500.00, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(1L, "Eletronics"));
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();
        return new ProductDTO(product);
    }

    public static Category createCategory(){
        return new Category(2L, "Electronics");
    }

    public static CategoryDTO createCategoryDTO(){
        return new CategoryDTO(createCategory());
    }
}
