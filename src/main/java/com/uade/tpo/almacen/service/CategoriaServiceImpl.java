package com.uade.tpo.almacen.service;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.entity.dto.CategoryRequest;
import com.uade.tpo.almacen.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repo;

    public CategoriaServiceImpl(CategoriaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<Categoria> getCategorias(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Optional<Categoria> getCategoriaById(Long id) {
        return repo.findById(id);
    }

    @Override
    public long countCategorias() {
        return repo.count();
    }

    @Override
    @Transactional
    public void deleteAllCategories() {
        repo.deleteAll();
    }

    @Override
    @Transactional
    public Categoria createCategory(CategoryRequest req) {
        Categoria cat = new Categoria();
        cat.setNombre(req.getNombre());

        if (req.getParentId() != null) {
            Categoria parent = repo.findById(req.getParentId().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría padre no encontrada: " + req.getParentId()));
            cat.setParentCategoria(parent);

            // asegurar que la lista de subcategorías no sea null
            if (parent.getSubcategorias() != null && !parent.getSubcategorias().contains(cat)) {
                parent.getSubcategorias().add(cat);
            }
        }

        return repo.save(cat);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public Categoria updateCategory(Long id, CategoryRequest req) {
        Categoria cat = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            cat.setNombre(req.getNombre());
        }

        if (req.getParentId() != null) {
            Categoria parent = repo.findById(req.getParentId().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría padre no encontrada: " + req.getParentId()));
            cat.setParentCategoria(parent);

            if (parent.getSubcategorias() != null && !parent.getSubcategorias().contains(cat)) {
                parent.getSubcategorias().add(cat);
            }
        } else {
            cat.setParentCategoria(null);
        }

        return repo.save(cat);
    }
}

