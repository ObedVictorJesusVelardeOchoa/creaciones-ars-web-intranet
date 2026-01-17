// Usuario.java - ELIMINAR los campos de correo y contraseña
package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Usuario")
    private int idUsuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Rol", nullable = false)
    private Rol rol;
    
    @Column(name = "nombre", nullable = false, length = 40)
    private String nombre;
    
    @Column(name = "apellido", nullable = false, length = 40)
    private String apellido;
    
    @Column(name = "DNI", nullable = false, length = 8)
    private String dni;
    
    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;
    
    @Column(name = "fecha_Nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Column(name = "fecha_Registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Estado_Usuario", nullable = false)
    private EstadoUsuario estadoUsuario;
    
    // ✅ ELIMINAR: No más campos de email y password aquí
    
    // Constructores
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public Usuario(String nombre, String apellido, String dni, String telefono, 
                   LocalDate fechaNacimiento, Rol rol, EstadoUsuario estadoUsuario) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.estadoUsuario = estadoUsuario;
    }
    
    // Getters y Setters (mantener solo los existentes, eliminar los de email y password)
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public EstadoUsuario getEstadoUsuario() {
        return estadoUsuario;
    }
    
    public void setEstadoUsuario(EstadoUsuario estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
    
    // Método útil para obtener nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // ✅ ELIMINAR: No más métodos setPassword() o similares
}