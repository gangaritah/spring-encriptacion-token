package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Usuario;
import com.example.demo.services.JwtService;
import com.example.demo.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(JwtService jwtService, PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> optionalUsuario = usuarioService.getById(
            usuarioService.getAll().stream()
                .filter(u -> u.getEmail().equals(loginRequest.getEmail()))
                .findFirst()
                .map(Usuario::getId)
                .orElse(null)
        );
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            if (passwordEncoder.matches(loginRequest.getContraseña(), usuario.getContraseña())) {
                String jwt = this.jwtService.generateJwtToken(usuario);
                return ResponseEntity.ok(new LoginResponse(jwt, usuario.getEmail(), usuario.getRol()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // Create
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        Usuario createdUsuario = usuarioService.create(usuario);
        return ResponseEntity.ok(createdUsuario);
    }

    // Read (All)
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = this.jwtService.extractToken(authHeader);
        if (token == null || !this.jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
        List<Usuario> usuarios = usuarioService.getAll();
        return ResponseEntity.ok(usuarios);
    }

    // Read (By ID)
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = this.jwtService.extractToken(authHeader);
        if (token == null || !this.jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
        return usuarioService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Usuario usuario, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = this.jwtService.extractToken(authHeader);
        if (token == null || !this.jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
        return usuarioService.update(id, usuario)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = this.jwtService.extractToken(authHeader);
        if (token == null || !this.jwtService.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing JWT token");
        }
        boolean deleted = usuarioService.delete(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Inner class for login request
    public static class LoginRequest {
        private String email;
        private String contraseña;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContraseña() {
            return contraseña;
        }

        public void setContraseña(String contraseña) {
            this.contraseña = contraseña;
        }
    }

    // Inner class for login response
    public static class LoginResponse {
        private String token;
        private String email;
        private String rol;

        public LoginResponse(String token, String email, String rol) {
            this.token = token;
            this.email = email;
            this.rol = rol;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }
    }
}


