package proyect.app.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import proyect.app.entity.Categoria;
import proyect.app.configuration.UsuarioPrincipal;
import proyect.app.dto.Login;
import proyect.app.entity.Usuarios;
import proyect.app.service.CategoriaService;
import proyect.app.service.UsuarioService;

@Controller
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("login", new Login());
        model.addAttribute("usuario", new Usuarios());
        return "index";
    }

    @PostMapping("/registro")
    public String registroUsuarios(@ModelAttribute Usuarios usuario, Model model) {
        if (usuarioService.buscarPorcorreoUsuario(usuario.getCorreoUsuario()) == null) {
            usuarioService.insertar(usuario); // solo aquí
            return "redirect:/users/login";
        } else {
            model.addAttribute("error", "El correo ya está registrado");
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/users/login?logout=true";
    }

    @GetMapping("/principal")
    public String mostrarPrincipal(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioPrincipal user = (UsuarioPrincipal) auth.getPrincipal();

        String nombreCompleto = user.getNombre();
        model.addAttribute("nombre", nombreCompleto);

        return "principal";
    }

    @GetMapping("/acercaDeNosotros")
    public String acercaDeNosotros(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            return "aboutUs";
        } else {
            return "redirect:/users/login";
        }
    }

    @GetMapping("/blog")
    public String blog(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            return "blog";
        } else {
            return "redirect:/users/login";
        }
    }

    @GetMapping("/access-denied")
    public String accesoDenegado() {
        return "access-denied";
    }

    @ModelAttribute("usuario")
    public String getUsuario(Principal principal) {
        return (principal != null) ? principal.getName() : null;
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin(Authentication authentication) {
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    // Categorías para mostrar en todas las vistas
    @ModelAttribute("categoriaHombres")
    public List<Categoria> getCategoriasHombres() {
        return categoriaService.listar().stream()
                .filter(c -> "Masculino".equals(c.getSexoCategoria()) || "Unisex".equals(c.getSexoCategoria()))
                .collect(Collectors.toList());
    }

    @ModelAttribute("categoriaMujeres")
    public List<Categoria> getCategoriasMujeres() {
        return categoriaService.listar().stream()
                .filter(c -> "Femenino".equals(c.getSexoCategoria()) || "Unisex".equals(c.getSexoCategoria()))
                .collect(Collectors.toList());
    }
}
