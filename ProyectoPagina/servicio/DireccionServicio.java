package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Direccion;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Usuario;
import java.util.List;

public interface DireccionServicio {
    List<Direccion> obtenerDireccionesPorUsuario(Usuario usuario);
    Direccion obtenerDireccionPorIdYUsuario(int idDireccion, Usuario usuario);
    void guardar(Direccion direccion);
    void eliminarDireccionPorIdYUsuario(int idDireccion, Usuario usuario);
    void establecerDireccionPrincipal(Usuario usuario, int idDireccion);
    void quitarPrincipalDeOtrasDirecciones(Usuario usuario);
}