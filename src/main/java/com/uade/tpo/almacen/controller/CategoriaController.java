package com.uade.tpo.almacen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.almacen.entity.Categoria;
import com.uade.tpo.almacen.controller.dto.CategoriaResponse; // ✅ DTO correcto
import com.uade.tpo.almacen.exception.NoEncontradoException;
import com.uade.tpo.almacen.exception.ParametroFueraDeRangoException;
import com.uade.tpo.almacen.service.CategoriaService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<Page<CategoriaResponse>> getCategorias(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page == null || size == null) {
            Page<Categoria> allCategorias = categoriaService.getCategorias(PageRequest.of(0, Integer.MAX_VALUE));

            if (allCategorias.getTotalElements() == 0) {
                throw new NoEncontradoException("No hay categorías cargadas.");
            }
            Page<CategoriaResponse> categoriasResponse = allCategorias.map(this::convertToCategoriaResponse);
            return ResponseEntity.ok(categoriasResponse);
        }

        if (page < 0 || size < 1) {
            throw new ParametroFueraDeRangoException("Los parámetros 'page' y 'size' deben ser mayores o iguales a 1.");
        }

        Page<Categoria> categorias = categoriaService.getCategorias(PageRequest.of(page, size));
        if (categorias.getTotalElements() == 0) {
            throw new NoEncontradoException("No hay categorías cargadas.");
        }

        Page<CategoriaResponse> categoriasResponse = categorias.map(this::convertToCategoriaResponse);
        return ResponseEntity.ok(categoriasResponse);
    }

    @GetMapping("/{categoriaID}")
    public ResponseEntity<CategoriaResponse> getCategoriaById(@PathVariable int categoriaID) {
        Optional<Categoria> result = categoriaService.getCategoriaById(categoriaID);
        if (result.isEmpty()) {
            throw new NoEncontradoException("La categoría con ID " + categoriaID + " no se encuentra.");
        }
        return ResponseEntity.ok(convertToCategoriaResponse(result.get()));
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(
            @RequestBody com.uade.tpo.almacen.entity.dto.CategoryRequest categoryRequest) {

        if (categoryRequest.getNombre() == null || categoryRequest.getNombre().trim().isEmpty()) {
            throw new ParametroFueraDeRangoException("El nombre de la categoría no puede estar vacío.");
        }
        if (categoryRequest.getParentId() != null && categoryRequest.getParentId() < 1) {
            throw new ParametroFueraDeRangoException("El ID de la categoría padre debe ser mayor o igual a 1.");
        }

        Categoria newCategory = categoriaService.createCategory(categoryRequest);
        return ResponseEntity.created(URI.create("/categories/" + newCategory.getId())).body(newCategory);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllCategories() {
        if (categoriaService.countCategorias() == 0) {
            throw new NoEncontradoException("No hay categorías para eliminar.");
        }
        categoriaService.deleteAllCategories();
        return ResponseEntity.ok("Todas las categorías fueron eliminadas correctamente.");
    }

    @DeleteMapping("/{categoriaID}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable int categoriaID) {
        Optional<Categoria> categoria = categoriaService.getCategoriaById(categoriaID);
        if (categoria.isEmpty()) {
            throw new NoEncontradoException("La categoría con ID " + categoriaID + " no se encuentra.");
        }
        categoriaService.deleteCategory(categoriaID);
        return ResponseEntity.ok("La categoría con ID " + categoriaID + " fue eliminada exitosamente.");
    }

    @PutMapping("/{categoriaID}")
    public ResponseEntity<Categoria> updateCategory(@PathVariable int categoriaID,
            @RequestBody com.uade.tpo.almacen.entity.dto.CategoryRequest categoryRequest) {
        Categoria updatedCategory = categoriaService.updateCategory(categoriaID, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    // ------- Mapper -------
    private CategoriaResponse convertToCategoriaResponse(Categoria categoria) {
        if (categoria == null) return null;

        List<CategoriaResponse> subcategoriasResponse = categoria.getSubcategorias() == null
                ? List.of()
                : categoria.getSubcategorias()
                           .stream()
                           .map(this::convertToCategoriaResponse)
                           .collect(Collectors.toList()); // evita .toList() si usás Java 8

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getParentCategoria() != null ? categoria.getParentCategoria().getId() : null,
                categoria.getParentCategoria() != null ? categoria.getParentCategoria().getNombre() : null,
                subcategoriasResponse
        );
    }
}
