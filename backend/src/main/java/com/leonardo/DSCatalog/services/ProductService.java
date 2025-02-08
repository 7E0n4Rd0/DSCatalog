package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.CategoryDTO;
import com.leonardo.DSCatalog.DTO.ProductDTO;
import com.leonardo.DSCatalog.entities.Category;
import com.leonardo.DSCatalog.entities.Product;
import com.leonardo.DSCatalog.repositories.ProductRepository;
import com.leonardo.DSCatalog.services.exceptions.DatabaseException;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
        Page<Product> result = repository.findAll(pageRequest);
        return result.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();

        entity = copyDTOtoEntity(dto, entity);

        repository.save(entity);
        return new ProductDTO(entity);
    }


    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {
            Product entity = repository.getReferenceById(id);
            entity = copyDTOtoEntity(dto, entity);
            repository.save(entity);
            return new ProductDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Resource not found. Id: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Resource not found.");
        }
        try{
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Fail on Referential Integrity");
        }
    }

    private Product copyDTOtoEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());
        entity.setMoment(dto.getMoment());

        for(CategoryDTO category : dto.getCategories()){
            entity.getCategories().add(new Category(category.getId(), category.getName()));
        }

        return entity;
    }

}
