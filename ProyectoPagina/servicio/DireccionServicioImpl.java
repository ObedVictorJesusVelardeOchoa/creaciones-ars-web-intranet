package com.ProyectoPaginaWeb.ProyectoPagina.servicio;

import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Direccion;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Usuario;
import com.ProyectoPaginaWeb.ProyectoPagina.repositorio.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DireccionServicioImpl implements DireccionServicio {

    @Autowired
    private DireccionRepository direccionRepository;

    @Override
    public List<Direccion> obtenerDireccionesPorUsuario(Usuario usuario) {
        return direccionRepository.findByUsuarioOrderByPrincipalDesc(usuario);
    }

    @Override
    public Direccion obtenerDireccionPorIdYUsuario(int idDireccion, Usuario usuario) {
        return direccionRepository.findByIdDireccionAndUsuario(idDireccion, usuario)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
    }

    @Override
    public void guardar(Direccion direccion) {
        direccionRepository.save(direccion);
    }

    @Override
    public void eliminarDireccionPorIdYUsuario(int idDireccion, Usuario usuario) {
        Direccion direccion = obtenerDireccionPorIdYUsuario(idDireccion, usuario);
        direccionRepository.delete(direccion);
    }

    @Override
    public void establecerDireccionPrincipal(Usuario usuario, int idDireccion) {
        // Primero, quitar el estado principal de todas las direcciones
        quitarPrincipalDeOtrasDirecciones(usuario);
        
        // Luego, establecer la nueva dirección como principal
        Direccion direccion = obtenerDireccionPorIdYUsuario(idDireccion, usuario);
        direccion.setPrincipal(true);
        direccionRepository.save(direccion);
    }

    @Override
    public void quitarPrincipalDeOtrasDirecciones(Usuario usuario) {
        List<Direccion> direccionesPrincipales = direccionRepository.findByUsuarioAndPrincipalTrue(usuario);
        for (Direccion direccion : direccionesPrincipales) {
            direccion.setPrincipal(false);
            direccionRepository.save(direccion);
        }
    }
}