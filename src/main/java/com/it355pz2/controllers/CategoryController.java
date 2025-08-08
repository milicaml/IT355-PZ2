package com.it355pz2.controllers;

import com.it355pz2.dto.CategoriesReasponse;
import com.it355pz2.entity.Category;
import com.it355pz2.services.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private CategoriesService categoriesService;

    @GetMapping("/")
    public ResponseEntity<List<CategoriesReasponse>> getCategories() {
        return ResponseEntity.ok(categoriesService.getCategories());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoriesReasponse> getCategory(Long id) {
        var category = categoriesService.getCategory(id);
        if (category == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(category);
    }
}
