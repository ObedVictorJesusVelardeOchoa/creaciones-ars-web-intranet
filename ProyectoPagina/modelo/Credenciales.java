package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Credenciales")
public class Credenciales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Credencial")
    private int idCredencial;
    
    @OneToOne
    @JoinColumn(name = "id_Usuario", nullable = false, unique = true)
    private Usuario usuario;
    
    @Column(name = "correo", nullable = false, unique = true, length = 255)
    private String correo;
    
    @Column(name = "contrasenia", length = 255)
    private String contrasenia;
    
    @Column(name = "codigo_Temporal", length = 255)
    private String codigoTemporal;
    
    @Column(name = "fecha_Expira_Codigo")
    private LocalDateTime fechaExpiraCodigo;
    
    // Constructores
    public Credenciales() {}
    
    public Credenciales(Usuario usuario, String correo, String contrasenia) {
        this.usuario = usuario;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }
    
    // Getters y Setters
    public int getIdCredencial() {
        return idCredencial;
    }
    
    public void setIdCredencial(int idCredencial) {
        this.idCredencial = idCredencial;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getContrasenia() {
        return contrasenia;
    }
    
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
    
    public String getCodigoTemporal() {
        return codigoTemporal;
    }
    
    public void setCodigoTemporal(String codigoTemporal) {
        this.codigoTemporal = codigoTemporal;
    }
    
    public LocalDateTime getFechaExpiraCodigo() {
        return fechaExpiraCodigo;
    }
    
    public void setFechaExpiraCodigo(LocalDateTime fechaExpiraCodigo) {
        this.fechaExpiraCodigo = fechaExpiraCodigo;
    }
}
