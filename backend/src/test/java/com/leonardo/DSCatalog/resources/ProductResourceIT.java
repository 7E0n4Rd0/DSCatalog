package com.leonardo.DSCatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.DSCatalog.DTO.ProductDTO;
import com.leonardo.DSCatalog.factory.Factory;
import com.leonardo.DSCatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;


    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }


    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(countTotalProducts));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedName));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedDescription));

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
