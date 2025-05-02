package proyect.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import proyect.app.dto.Login;
import proyect.app.entity.Usuarios;
import proyect.app.repository.UsuarioRepository;

@RestController
@RequestMapping("/api")
public class restcontroller {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private Environment enviromen;

    @GetMapping("/usuarios")
    public Map<String, Object> getAll() {
        Map<String, Object> usuarios = new HashMap<>();

        int index = 1;
        for (Usuarios usuario : usuarioRepository.findAll()) {
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("id", usuario.getIdUsuario());
            usuarioMap.put("nombre", usuario.getNombreUsuario());
            usuarioMap.put("apellido", usuario.getApellidoUsuario());
            usuarioMap.put("celular", usuario.getNumeroTelefonoUsuario());
            usuarioMap.put("correo", usuario.getCorreoUsuario());
            usuarioMap.put("contrasenia", usuario.getContrasenaUsuario());
            usuarios.put("usuario_" + index++, usuarioMap);
        }

        if (usuarios.isEmpty()) {
            return Map.of("mensaje", "No hay usuarios registrados");
        }

        return Map.of("usuarios", usuarios);
    }

    @GetMapping("/usuarios/contar")
    public Map<String, Object> contarUsuarios() {
        long count = usuarioRepository.count();
        if (count == 0) {
            return Map.of("mensaje", "No hay usuarios registrados");
        }
        return Map.of("total_usuarios", count);
    }

    @PostMapping("/usuarios/insertar")
    public Map<String, Object> insertarUsuario(@RequestBody Usuarios usuario) {
        if (usuario.getNombreUsuario() == null || usuario.getCorreoUsuario() == null
                || usuario.getContrasenaUsuario() == null) {
            return Map.of("mensaje", "Faltan datos para registrar el usuario");
        }
        if (usuarioRepository.findByCorreoUsuario(usuario.getCorreoUsuario()).isPresent()) {
            return Map.of("mensaje", "El correo ya está registrado");
        }
        if (usuario.getContrasenaUsuario().length() == 9) {
            return Map.of("mensaje", "La contraseña debe tener 9 caracteres");
        }
        Usuarios nuevoUsuario = usuarioRepository.save(usuario);
        return Map.of("mensaje", "Usuario registrado con éxito", "usuario", nuevoUsuario);
    }

    @PostMapping("/usuarios/login")
    public Map<String, Object> login(@RequestBody Login loginDTO) {
        if (loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
            return Map.of("mensaje", "Faltan datos para iniciar sesión");
        }

        Usuarios usuarioEncontrado = usuarioRepository.findByCorreoUsuario(loginDTO.getEmail()).orElse(null);
        if (usuarioEncontrado == null) {
            return Map.of("mensaje", "Usuario no encontrado");
        }

        if (!usuarioEncontrado.getContrasenaUsuario().equals(loginDTO.getPassword())) {
            return Map.of("mensaje", "Contraseña incorrecta");
        }

        return Map.of("mensaje", "Inicio de sesión exitoso", "usuario", usuarioEncontrado);
    }

    /*
     * @PostMapping("/login")
     * public ResponseEntity<?> logeo(@RequestBody Login login) {
     * usuarios usuario =
     * usuarioRepository.findByCorreoUsuario(login.getEmail()).orElse(null);
     * 
     * if (usuario == null ||
     * !usuario.getContrasenaUsuario().equals(login.getPassword())) {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
     * .body(Map.of("mensaje", "Credenciales inválidas"));
     * }
     * 
     * String tokengenerated = token.generateToken(usuario.getCorreoUsuario());
     * 
     * return ResponseEntity.ok(Map.of(
     * "token", tokengenerated,
     * "usuario", usuario));
     * }
     */
    @GetMapping("/values")
    public Map<String, Object> getValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("mensaje", enviromen.getProperty("config.message"));
        values.put("CPU", enviromen.getProperty("config.mapeo", List.class));
        return values;
    }

    @GetMapping("/datos")
    public Usuarios getDatos(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String celular,
            @RequestParam String correo,
            @RequestParam String contrasena) {

        Usuarios usuario = new Usuarios();
        usuario.setNombreUsuario(nombre);
        usuario.setApellidoUsuario(apellido);
        usuario.setNumeroTelefonoUsuario(Integer.parseInt(celular));
        usuario.setCorreoUsuario(correo);
        usuario.setContrasenaUsuario(contrasena);
        return usuario;
    }

    @GetMapping("/guardar/correo")
    public ResponseEntity<?> guardarCorreoEnSesion(@RequestParam String correo, HttpSession session) {
        session.setAttribute("correo", correo);
        Optional<Usuarios> usuarioOpt = usuarioRepository.findByCorreoUsuario(correo);
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Correo guardado en sesión");
        response.put("usuario", usuarioOpt.get());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/datos/usuario")
    public ResponseEntity<?> obtenerDatosUsuarioDesdeSesion(HttpSession sesion) {
        String correo = (String) sesion.getAttribute("correo");

        if (correo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "No se encontró correo en la sesión"));
        }

        Optional<Usuarios> usuarioOpt = usuarioRepository.findByCorreoUsuario(correo);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Usuario no encontrado con el correo: " + correo));
        }
    }

    @GetMapping("/datos/nombre")
    public ResponseEntity<?> obtenerNombreDesdeSesion(HttpSession sesion) {
        String nombre , apellido;
        String correo = (String) sesion.getAttribute("correo");

        if (correo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "No se encontró nombre en la sesión"));
        }
        
        Optional<Usuarios> usuarioOpt = usuarioRepository.findByCorreoUsuario(correo);
        if (usuarioOpt.isPresent()) {
            nombre = usuarioOpt.get().getNombreUsuario();
            apellido = usuarioOpt.get().getApellidoUsuario();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Usuario no encontrado con el correo: " +correo));
        }

        return ResponseEntity.ok(Map.of("nombre", nombre, "apellido", apellido));
    }
}
