package com.it355pz2.services;

import com.it355pz2.dto.CategoriesReasponse;
import com.it355pz2.entity.Category;
import lombok.AllArgsConstructor;

import java.util.List;

public interface CategoriesService {
    List<CategoriesReasponse> getCategories();
    CategoriesReasponse getCategory(Long id);
}
