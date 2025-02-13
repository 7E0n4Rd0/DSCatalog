package com.leonardo.DSCatalog.repositories;

import com.leonardo.DSCatalog.entities.Product;
import com.leonardo.DSCatalog.factory.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;
    
    long existingId;
    long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){

        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts+1, product.getId());
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalProductWhenIdExists(){

        Optional<Product> product = repository.findById(existingId);

        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalProductWhenIdDoesNotExists(){
        Optional<Product> product = repository.findById(28L);

        Assertions.assertTrue(product.isEmpty());
    }
}
