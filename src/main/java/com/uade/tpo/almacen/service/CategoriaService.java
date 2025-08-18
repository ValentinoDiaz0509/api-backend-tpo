package com.uade.tpo.almacen.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.dto.CategoryRequest;

public interface CategoriaService {
    Page<Categoria> getCategorias(Pageable pageable);
    Optional<Categoria> getCategoriaById(int id);
    Categoria createCategory(CategoryRequest request);
    void deleteAllCategories();
    long countCategorias();
    void deleteCategory(int id);
    Categoria updateCategory(int id, CategoryRequest request);
}
