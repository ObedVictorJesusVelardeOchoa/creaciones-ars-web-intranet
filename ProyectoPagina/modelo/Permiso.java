package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "Permiso")
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Permiso")
    private int idPermiso;
    
    @Column(name = "nombre_Permiso", nullable = false, length = 50)
    private String nombrePermiso;
    
    // Constructores
    public Permiso() {}
    
    public Permiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }
    
    // Getters y Setters
    public int getIdPermiso() {
        return idPermiso;
    }
    
    public void setIdPermiso(int idPermiso) {
        this.idPermiso = idPermiso;
    }
    
    public String getNombrePermiso() {
        return nombrePermiso;
    }
    
    public void setNombrePermiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }
    
    @Override
    public String toString() {
        return nombrePermiso;
    }
}