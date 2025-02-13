package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.CategoryDTO;
import com.leonardo.DSCatalog.DTO.ProductDTO;
import com.leonardo.DSCatalog.entities.Category;
import com.leonardo.DSCatalog.entities.Product;
import com.leonardo.DSCatalog.factory.Factory;
import com.leonardo.DSCatalog.repositories.CategoryRepository;
import com.leonardo.DSCatalog.repositories.ProductRepository;
import com.leonardo.DSCatalog.services.exceptions.DatabaseException;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServicesTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 2L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();
        categoryDTO = Factory.createCategoryDTO();

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save((Product) ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.doReturn(category).when(categoryRepository).getReferenceById(ArgumentMatchers.any());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

    }

    @Test
    public void findAllPagedShouldReturnPage(){

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);

        Mockito.verify(repository).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNonExists(){
       Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           service.findById(nonExistingId);
       });
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists(){
        ProductDTO productDTO = Factory.createProductDTO();
        ProductDTO result = service.update(existingId, productDTO);
        Assertions.assertNotNull(result);

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdNonExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
            ProductDTO productDTO = Factory.createProductDTO();
            service.update(nonExistingId, productDTO);
        });
    }

}
