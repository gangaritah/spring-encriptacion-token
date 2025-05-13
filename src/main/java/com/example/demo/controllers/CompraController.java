package com.example.demo.controllers;

import java.util.List;
import java.util.Optional; // Necesario para Optional<Usuario>

import org.springframework.beans.factory.annotation.Autowired; // Para la inyección de dependencias
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Compra;
import com.example.demo.models.Usuario;
import com.example.demo.services.JwtService;
import com.example.demo.services.UsuarioService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @PersistenceContext
    private EntityManager entityManager;

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Autowired
    public CompraController(JwtService jwtService, UsuarioService usuarioService, EntityManager entityManager) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.entityManager = entityManager; 
    }

    // Create
    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Compra compra, @RequestHeader(value = "Authorization") String authHeader) {
        String token = jwtService.extractToken(authHeader);
        if (token == null || !jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o ausente.");
        }

        String userEmail = jwtService.getEmailFromToken(token);
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo extraer el email del token.");
        }

        Optional<Usuario> optionalUsuario = usuarioService.getAll().stream()
                .filter(u -> u.getEmail().equals(userEmail))
                .findFirst();

        if (!optionalUsuario.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario asociado al token no encontrado.");
        }

        Usuario usuario = optionalUsuario.get();
        compra.setIdUsuario(usuario.getId());


        entityManager.persist(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body(compra);
    }

    // Read (All)
    @Transactional
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtService.extractToken(authHeader);
        if (token == null || !jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o ausente.");
        }
  
        Query query = entityManager.createNativeQuery("SELECT * FROM compras", Compra.class);
        List<Compra> compras = query.getResultList();
        return ResponseEntity.ok(compras);
    }

    // Read (By ID)
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtService.extractToken(authHeader);
        if (token == null || !jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o ausente.");
        }

        Query query = entityManager.createNativeQuery("SELECT * FROM compras WHERE id = :id", Compra.class);
        query.setParameter("id", id);
        try {
            Compra compra = (Compra) query.getSingleResult();
            return ResponseEntity.ok(compra);
        } catch (NoResultException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Compra compraDetails, @RequestHeader(value = "Authorization") String authHeader) {
        String token = jwtService.extractToken(authHeader);
        if (token == null || !jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o ausente.");
        }

        Query query = entityManager.createNativeQuery(
                "UPDATE compras SET id_usuario = :idUsuario, id_producto = :idProducto, fecha_compra = :fechaCompra, cantidad = :cantidad WHERE id = :id");
        

        Compra compraExistente = entityManager.find(Compra.class, id);
        if (compraExistente == null) {
            return ResponseEntity.notFound().build();
        }

        query.setParameter("idUsuario", compraDetails.getIdUsuario() != null ? compraDetails.getIdUsuario() : compraExistente.getIdUsuario()); // Mantener el idUsuario original si no se provee uno nuevo
        query.setParameter("idProducto", compraDetails.getIdProducto());
        query.setParameter("fechaCompra", compraDetails.getFechaCompra());
        query.setParameter("cantidad", compraDetails.getCantidad());
        query.setParameter("id", id);
        
        int updated = query.executeUpdate();
        
        if (updated > 0) {

            Compra compraActualizada = entityManager.find(Compra.class, id);
            return ResponseEntity.ok(compraActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestHeader(value = "Authorization") String authHeader) {
        String token = jwtService.extractToken(authHeader);
        if (token == null || !jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT inválido o ausente.");
        }


        Query query = entityManager.createNativeQuery("DELETE FROM compras WHERE id = :id");
        query.setParameter("id", id);
        int deleted = query.executeUpdate();
        return deleted > 0 ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); // noContent() es más común para DELETE exitoso
    }
}