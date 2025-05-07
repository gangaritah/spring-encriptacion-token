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

import com.example.demo.models.Compra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @PersistenceContext
    private EntityManager entityManager;

    // Create
    @Transactional
    @PostMapping
    public ResponseEntity<Compra> create(@RequestBody Compra compra) {
        entityManager.persist(compra);
        return ResponseEntity.ok(compra);
    }

    // Read (All)
    @Transactional
    @GetMapping
    public List<Compra> getAll() {
        Query query = entityManager.createNativeQuery("SELECT * FROM compras", Compra.class);
        return query.getResultList();
    }

    // Read (By ID)
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<Compra> getById(@PathVariable Integer id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM compras WHERE id = :id", Compra.class);
        query.setParameter("id", id);
        Compra compra = (Compra) query.getSingleResult();
        return compra != null ? ResponseEntity.ok(compra) : ResponseEntity.notFound().build();
    }

    // Update
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Compra> update(@PathVariable Integer id, @RequestBody Compra compra) {
        Query query = entityManager.createNativeQuery(
            "UPDATE compras SET id_usuario = :idUsuario, id_producto = :idProducto, fecha_compra = :fechaCompra, cantidad = :cantidad WHERE id = :id"
        );
        query.setParameter("idUsuario", compra.getIdUsuario());
        query.setParameter("idProducto", compra.getIdProducto());
        query.setParameter("fechaCompra", compra.getFechaCompra());
        query.setParameter("cantidad", compra.getCantidad());
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        return updated > 0 ? ResponseEntity.ok(compra) : ResponseEntity.notFound().build();
    }

    // Delete
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Query query = entityManager.createNativeQuery("DELETE FROM compras WHERE id = :id");
        query.setParameter("id", id);
        int deleted = query.executeUpdate();
        return deleted > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
