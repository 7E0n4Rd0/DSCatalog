package com.leonardo.DSCatalog.services;

import com.leonardo.DSCatalog.entities.Category;
import com.leonardo.DSCatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<Category> findAll(){
        List<Category> result = repository.findAll();
        return result;
    }

}
