package com.it355pz2.services.impl;

import com.it355pz2.dto.CategoriesReasponse;
import com.it355pz2.entity.Category;
import com.it355pz2.repository.CategoryRepository;
import com.it355pz2.services.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoriesServiceImpl implements CategoriesService {
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoriesReasponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoriesReasponse::new).toList();
    }

    @Override
    public CategoriesReasponse getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null) return null;
        return new CategoriesReasponse(category);
    }
}
