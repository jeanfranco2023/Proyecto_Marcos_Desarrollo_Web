package proyect.app.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import proyect.app.dto.Login;
import proyect.app.entity.Categoria;
import proyect.app.entity.Usuarios;
import proyect.app.repository.UsuarioRepository;
import proyect.app.service.CategoriaService;
import proyect.app.service.UsuarioService;

@Controller
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    private Usuarios usuarioIniciado;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("login", new Login());
        model.addAttribute("usuario", new Usuarios());
        return "index";
    }

    @PostMapping("/logeo")
    public String login(Login login, RedirectAttributes redirectAttributes, HttpSession usuario) {

        Optional<Usuarios> usuarioOptional = usuarioRepository.findByCorreoUsuario(login.getEmail());

        if (usuarioOptional.isPresent()) {
            usuarioIniciado = usuarioOptional.get();
            usuario.setAttribute("usuarioIniciado", usuarioIniciado.getNombreUsuario());
            usuario.setAttribute("administrador", usuarioIniciado.isAdmin());
            String contrasenia = usuarioIniciado.getContrasenaUsuario();
            if (contrasenia.equals(login.getPassword())) {
                return "redirect:/users/principal";
            } else {
                redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        }
        return "redirect:/users/login";
    }

    @PostMapping("/registro")
    public String registroUsuarios(@ModelAttribute Usuarios usuario, Model model) {
        if (usuarioService.buscarPorcorreoUsuario(usuario.getCorreoUsuario()) == null) {
            usuarioService.insertar(usuario);
            return "redirect:/users/login";
        } else {
            model.addAttribute("error", "El correo ya está registrado");
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    @GetMapping("/principal")
    public String principal(Model model) {
        if (usuarioIniciado != null) {
            return "principal";
        } else {
            return "redirect:/users/login";
        }

    }

    @GetMapping("/acercaDeNosotros")
    public String acercaDeNosotros(Model model) {
        if (usuarioIniciado != null) {
            return "aboutUs";
        } else {
            return "redirect:/users/login";
        }
    }

    @GetMapping("/blog")
    public String blog(Model model) {
        if (usuarioIniciado != null) {
            return "blog";
        } else {
            return "redirect:/users/login";
        }
    }

    @ModelAttribute("categoriaHombres")
    public List<Categoria> getCategoriasHombres() {
        List<Categoria> categorias = categoriaService.listar();
        List<Categoria> categoriasHombres = categorias.stream()
                .filter(categoria -> "Masculino".equals(categoria.getSexoCategoria()) ||
                        "Unisex".equals(categoria.getSexoCategoria()))
                .collect(Collectors.toList());
        return categoriasHombres;
    }

    @ModelAttribute("categoriaMujeres")
    public List<Categoria> getCategoriasMujeres() {
        List<Categoria> categorias = categoriaService.listar();
        List<Categoria> categoriasMujeres = categorias.stream()
                .filter(categoria -> "Femenino".equals(categoria.getSexoCategoria()) ||
                        "Unisex".equals(categoria.getSexoCategoria()))
                .collect(Collectors.toList());
        return categoriasMujeres;
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin(HttpSession session) {
        Boolean admin = (Boolean) session.getAttribute("administrador");
        return admin != null && admin;
    }

    @ModelAttribute("usuario")
    public String getUsuarioIniciado(HttpSession session) {
        Object usuario = session.getAttribute("usuarioIniciado");
        return usuario != null ? usuario.toString() : null;
    }
}
