package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.DTO.CategoryDTO;
import com.leonardo.DSCatalog.entities.Category;
import com.leonardo.DSCatalog.repositories.CategoryRepository;
import com.leonardo.DSCatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<CategoryDTO> result = repository.findAll().stream().map(CategoryDTO::new).toList();
        return result;
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return new CategoryDTO(entity);
    }
}
