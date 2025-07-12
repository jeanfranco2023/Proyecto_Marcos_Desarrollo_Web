package proyect.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import proyect.app.dto.Login;
import proyect.app.dto.ProductoDTO;
import proyect.app.entity.Categoria;
import proyect.app.entity.Productos;
import proyect.app.entity.Usuarios;
import proyect.app.repository.UsuarioRepository;
import proyect.app.service.CategoriaService;
import proyect.app.service.ProductoService;
import proyect.app.service.UsuarioService;

@RestController
@RequestMapping("/api")
@CrossOrigin(value = "http://localhost:4200")
public class restcontroller {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/usuarios")
    public List<Usuarios> getAll() {
        List<Usuarios> usuarios = usuarioRepository.findAll();
        return usuarios;
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
    public ResponseEntity<?> insertarUsuario(@RequestBody Usuarios usuario) {
        if (usuario.getNombreUsuario() == null || usuario.getCorreoUsuario() == null
                || usuario.getContrasenaUsuario() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Faltan datos para registrar el usuario"));
        }

        if (usuarioRepository.findByCorreoUsuario(usuario.getCorreoUsuario()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "El correo ya está registrado"));
        }

        if (usuario.getContrasenaUsuario().length() < 9) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "La contraseña debe tener al menos 9 caracteres"));
        }

        usuario.setContrasenaUsuario(passwordEncoder.encode(usuario.getContrasenaUsuario()));

        Usuarios nuevoUsuario = usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado con éxito", "usuario", nuevoUsuario));
    }


    @PostMapping("/usuarios/actualizar")
    public Map<String, Object> actualizarUsuario(@RequestBody Usuarios usuario) {
        if (usuario.getIdUsuario() == null) {
            return Map.of("mensaje", "Faltan datos para actualizar el usuario");
        }
        Optional<Usuarios> usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario());
        if (usuarioExistente.isPresent()) {
            Usuarios usuarioActualizado = usuarioRepository.save(usuario);
            return Map.of("mensaje", "Usuario actualizado con éxito", "usuario", usuarioActualizado);
        } else {
            return Map.of("mensaje", "Usuario no encontrado");
        }
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
        String nombre, apellido;
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
                    .body(Map.of("mensaje", "Usuario no encontrado con el correo: " + correo));
        }

        return ResponseEntity.ok(Map.of("nombre", nombre, "apellido", apellido));
    }

    @GetMapping("/detalles")
    public List<ProductoDTO> listarProductosConDetalles() {
        return productoService.listar().stream()
                .map(ProductoDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/productos/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody Productos producto) {
        if (producto.getNombreProducto() == null || producto.getPrecioProducto() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Faltan datos para registrar el producto"));
        }
        if (productoService.buscarPorNombreProducto(producto.getNombreProducto()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "El producto ya está registrado"));
        }
        productoService.insertar(producto);
        return ResponseEntity.ok(Map.of("mensaje", "Producto registrado con éxito"));
    }

    @GetMapping("/categorias")
    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = categoriaService.listar();
        return categorias;
    }

    @GetMapping("/usuarios/filtrar/{id}")
    public ResponseEntity<Usuarios> filtrarUsuariosPorId(@PathVariable Integer id) {
        Usuarios usuario = this.usuarioService.buscarPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
