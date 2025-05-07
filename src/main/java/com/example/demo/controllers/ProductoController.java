package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Producto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @PersistenceContext
    private EntityManager entityManager;

    // Create
    @Transactional
    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        entityManager.persist(producto);
        return ResponseEntity.ok(producto);
    }

    // Read (All)
    @Transactional
    @GetMapping
    public List<Producto> getAll() {
        Query query = entityManager.createNativeQuery("SELECT * FROM productos", Producto.class);
        return query.getResultList();
    }

    // Read (By ID)
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Integer id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM productos WHERE id = :id", Producto.class);
        query.setParameter("id", id);
        Producto producto = (Producto) query.getSingleResult();
        return producto != null ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    // Update
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Integer id, @RequestBody Producto producto) {
        Query query = entityManager.createNativeQuery(
            "UPDATE productos SET nombre = :nombre, precio = :precio, categoria = :categoria, urlImagen = :urlImagen WHERE id = :id"
        );
        query.setParameter("nombre", producto.getNombre());
        query.setParameter("precio", producto.getPrecio());
        query.setParameter("categoria", producto.getCategoria());
        query.setParameter("urlImagen", producto.getUrlImagen());
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        return updated > 0 ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    // Delete
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Query query = entityManager.createNativeQuery("DELETE FROM productos WHERE id = :id");
        query.setParameter("id", id);
        int deleted = query.executeUpdate();
        return deleted > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}