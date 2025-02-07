package com.leonardo.DSCatalog.resources;

import com.leonardo.DSCatalog.entities.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {


    @GetMapping
    public ResponseEntity<List<Category>> findAll(){
       List<Category> result = new ArrayList<>();
       result.add(new Category(01L, "Books"));
       result.add(new Category(02L, "Eletronics"));
       return ResponseEntity.ok(result);
    }

}
