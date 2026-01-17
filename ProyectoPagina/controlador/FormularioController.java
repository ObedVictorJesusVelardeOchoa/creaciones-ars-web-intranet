package com.ProyectoPaginaWeb.ProyectoPagina.controlador;


import com.ProyectoPaginaWeb.ProyectoPagina.modelo.Mensaje;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/formulario")
public class FormularioController {

    // GET - muestra el formulario
    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("mensaje", new Mensaje());
        return "formulario"; // renderiza templates/formulario.html
    }

    // POST - procesa el formulario
    @PostMapping("/enviar")
    public String procesarFormulario(@ModelAttribute("mensaje") Mensaje mensaje, Model model) {
        // En el futuro aquí se puede guardar en base de datos
        System.out.println("Mensaje recibido:");
        System.out.println("Nombre: " + mensaje.getNombre());
        System.out.println("Email: " + mensaje.getEmail());
        System.out.println("Asunto: " + mensaje.getAsunto());
        System.out.println("Contenido: " + mensaje.getContenido());

        model.addAttribute("exito", "Tu mensaje ha sido enviado correctamente");
        return "formulario"; // vuelve a la misma vista
    }
}


