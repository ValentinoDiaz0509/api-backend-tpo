package com.uade.tpo.almacen.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.dto.CategoryRequest;
import com.uade.tpo.almacen.repository.CategoriaRepository;
import com.uade.tpo.almacen.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Page<Categoria> getCategorias(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    @Override
    public Optional<Categoria> getCategoriaById(int id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria createCategory(CategoryRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        if (request.getParentId() != null) {
            categoriaRepository.findById(request.getParentId())
                    .ifPresent(categoria::setParentCategoria);
        }
        return categoriaRepository.save(categoria);
    }

    @Override
    public void deleteAllCategories() {
        categoriaRepository.deleteAll();
    }

    @Override
    public long countCategorias() {
        return categoriaRepository.count();
    }

    @Override
    public void deleteCategory(int id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public Categoria updateCategory(int id, CategoryRequest request) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow();
        categoria.setNombre(request.getNombre());
        if (request.getParentId() != null) {
            categoriaRepository.findById(request.getParentId())
                    .ifPresent(categoria::setParentCategoria);
        } else {
            categoria.setParentCategoria(null);
        }
        return categoriaRepository.save(categoria);
    }
}
