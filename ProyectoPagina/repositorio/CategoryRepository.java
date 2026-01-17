package com.ProyectoPaginaWeb.ProyectoPagina.repositorio;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Category;
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    
}
