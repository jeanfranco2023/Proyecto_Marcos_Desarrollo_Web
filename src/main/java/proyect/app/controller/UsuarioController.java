package proyect.app.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import proyect.app.dto.Login;
import proyect.app.entity.Usuarios;
import proyect.app.repository.UsuarioRepository;
import proyect.app.service.UsuarioService;

@Controller
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    private Usuarios usuarioIniciado;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("login", new Login());
        model.addAttribute("usuario", new Usuarios());
        return "index";
    }

    @PostMapping("/logeo")
    public String login(Login login,  RedirectAttributes redirectAttributes) {
        
        Optional<Usuarios> usuarioOptional = usuarioRepository.findByCorreoUsuario(login.getEmail());

        if (usuarioOptional.isPresent()) {
            usuarioIniciado = usuarioOptional.get();
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
    public String registroUsuarios(@ModelAttribute("usuario") Usuarios usuario, Model model) {
        if (usuarioService.buscarPorcorreoUsuario(usuario.getCorreoUsuario()) == null) {
            usuarioService.insertar(usuario);
            return "redirect:/users/login";
        } else {
            model.addAttribute("error", "El correo ya está registrado");
            return "index";
        }
    }

    @GetMapping("/principal")
    public String principal(Model model, HttpServletRequest respuesta) {
        if (usuarioIniciado != null) {
            model.addAttribute("usuario", usuarioIniciado.getNombreUsuario());
            return "principal";
        } else {
            return "redirect:/users/login";
        }
        
    }
}
