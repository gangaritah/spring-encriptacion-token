package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "Compras")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();

    @Column(nullable = false)
    private Integer cantidad;

    // Constructor por defecto (necesario para JPA y frameworks como Jackson)
    public Compra() {
    }

    // --- Getters ---

    public Integer getId() {
        return id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    // --- Setters ---

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}