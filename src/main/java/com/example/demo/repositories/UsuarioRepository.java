package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.models.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;


@Repository
public class UsuarioRepository {
   
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public Usuario create(Usuario usuario) {
        entityManager.persist(usuario);
        return usuario;	
    }
    
    @Transactional
    public List<Usuario> findAll() {
        Query query = entityManager.createNativeQuery("SELECT * FROM usuarios", Usuario.class);
        return query.getResultList();
    }
    
    @Transactional
    public Optional<Usuario> findById(Integer id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM usuarios WHERE id = :id", Usuario.class);
        query.setParameter("id", id);
        try {
            Usuario usuario = (Usuario) query.getSingleResult();
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Transactional
    public Optional<Usuario> update(Integer id, Usuario usuario) {
        Query query = entityManager.createNativeQuery(
            "UPDATE usuarios SET nombres = :nombres, apellidos = :apellidos, contraseña = :contraseña, email = :email, rol = :rol WHERE id = :id"
        );
        query.setParameter("nombres", usuario.getNombres());
        query.setParameter("apellidos", usuario.getApellidos());
        query.setParameter("contraseña", usuario.getContraseña());
        query.setParameter("email", usuario.getEmail());
        query.setParameter("rol", usuario.getRol());
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        if (updated > 0) {
            return findById(id);
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean delete(Integer id) {
        Query query = entityManager.createNativeQuery("DELETE FROM usuarios WHERE id = :id");
        query.setParameter("id", id);
        int deleted = query.executeUpdate();
        return deleted > 0;
    }
}
