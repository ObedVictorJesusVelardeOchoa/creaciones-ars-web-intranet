package com.ProyectoPaginaWeb.ProyectoPagina.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "Direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Direccion")
    private int idDireccion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Usuario", nullable = false)
    private Usuario usuario;
    
    @Column(name = "nombre_Direccion", nullable = false, length = 50)
    private String nombreDireccion;
    
    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;
    
    @Column(name = "numero", nullable = false, length = 20)
    private String numero;
    
    @Column(name = "referencia", length = 100)
    private String referencia;
    
    @Column(name = "departamento", nullable = false, length = 50)
    private String departamento = "Lima";
    
    @Column(name = "provincia", nullable = false, length = 50)
    private String provincia = "Lima";
    
    @Column(name = "distrito", nullable = false, length = 50)
    private String distrito;
    
    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;
    
    @Column(name = "principal", nullable = false)
    private boolean principal = false;
    
    // En el constructor, asegúrate de inicializar los campos de Lima
    public Direccion() {
        this.departamento = "Lima";
        this.provincia = "Lima";
    }
    
    public Direccion(Usuario usuario, String nombreDireccion, String direccion, String numero, 
                    String referencia, String distrito, String telefono, boolean principal) {
        this.usuario = usuario;
        this.nombreDireccion = nombreDireccion;
        this.direccion = direccion;
        this.numero = numero;
        this.referencia = referencia;
        this.distrito = distrito;
        this.telefono = telefono;
        this.principal = principal;
    }
    
    // Getters y Setters
    public int getIdDireccion() {
        return idDireccion;
    }
    
    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getNombreDireccion() {
        return nombreDireccion;
    }
    
    public void setNombreDireccion(String nombreDireccion) {
        this.nombreDireccion = nombreDireccion;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public String getReferencia() {
        return referencia;
    }
    
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getDistrito() {
        return distrito;
    }
    
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public boolean isPrincipal() {
        return principal;
    }
    
    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
    
    // Método útil para obtener la dirección completa
    public String getDireccionCompleta() {
        return direccion + " " + numero + (referencia != null ? ", " + referencia : "") + 
               ", " + distrito + ", " + provincia + ", " + departamento;
    }
}
