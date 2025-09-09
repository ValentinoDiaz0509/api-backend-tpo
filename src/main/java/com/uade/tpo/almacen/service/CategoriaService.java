package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.dto.CategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoriaService {

    Page<Categoria> getCategorias(Pageable pageable);

    Optional<Categoria> getCategoriaById(Long id);  

    long countCategorias();

    void deleteAllCategories();

    Categoria createCategory(CategoryRequest req);

    void deleteCategory(Long id);  

    Categoria updateCategory(Long id, CategoryRequest req);  
}
