package com.shopease.service;

import com.shopease.dto.CategoryRequest;
import com.shopease.exception.DuplicateResourceException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.model.Category;
import com.shopease.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .createdAt(LocalDateTime.now())
                .build();

        return categoryRepository.save(category);
    }

    public Category updateCategory(String id, CategoryRequest request) {
        Category category = getCategoryById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
