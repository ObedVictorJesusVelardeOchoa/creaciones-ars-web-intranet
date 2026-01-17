package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_usuario")
public class EstadoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Estado_Usuario")
    private Integer id;
    
    @Column(name = "nombre_Estado", nullable = false, length = 50)
    private String nombreEstado;
    
    // Constructores
    public EstadoUsuario() {}
    
    public EstadoUsuario(Integer id, String nombreEstado) {
        this.id = id;
        this.nombreEstado = nombreEstado;
    }
    
    public EstadoUsuario(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
    
    // Getters y Setters - Asegúrate de que estos estén correctos
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombreEstado() {
        return nombreEstado;
    }
    
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
    
    @Override
    public String toString() {
        return "EstadoUsuario{" +
                "id=" + id +
                ", nombreEstado='" + nombreEstado + '\'' +
                '}';
    }
}