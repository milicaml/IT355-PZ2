package com.it355pz2.dto;

import com.it355pz2.entity.Category;
import lombok.Data;

@Data
public class CategoriesReasponse {
    private String title;
    private String description;

    public CategoriesReasponse(Category category) {
        this.title = category.getTitle();
        this.description = category.getDescription();
    }
}
