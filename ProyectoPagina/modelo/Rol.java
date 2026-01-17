package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Rol")
    private int idRol;
    
    @Column(name = "nombre_Rol", nullable = false, length = 40)
    private String nombreRol;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "Rol_Permiso",
        joinColumns = @JoinColumn(name = "id_Rol"),
        inverseJoinColumns = @JoinColumn(name = "id_Permiso")
    )
    private Set<Permiso> permisos = new HashSet<>();
    
    // Constructores
    public Rol() {}
    
    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    // Getters y Setters
    public int getIdRol() {
        return idRol;
    }
    
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    
    public String getNombreRol() {
        return nombreRol;
    }
    
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    public Set<Permiso> getPermisos() {
        return permisos;
    }
    
    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }
}
