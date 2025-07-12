package proyect.app.configuration;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import proyect.app.entity.Usuarios;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UsuarioPrincipal implements UserDetails {

    private final Usuarios usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.isAdmin()
                ? List.of(() -> "ROLE_ADMIN")
                : List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return usuario.getContrasenaUsuario();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreoUsuario();
    }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override public boolean isAccountNonLocked() { return true; }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override public boolean isEnabled() { return true; }

    public String getNombreCompleto() {
        return usuario.getNombreUsuario() + " " + usuario.getApellidoUsuario();
    }

    public String getNombre(){
        return usuario.getNombreUsuario();
    }

    public Usuarios getUsuario() {
        return usuario;
    }
}
