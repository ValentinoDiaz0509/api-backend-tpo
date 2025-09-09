package com.uade.tpo.almacen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.almacen.controller.dto.LoginJwtResponse;
import com.uade.tpo.almacen.controller.dto.LoginRequest;
import com.uade.tpo.almacen.controller.dto.UsuarioLoginResponse;
import com.uade.tpo.almacen.entity.Rol;
import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.security.JwtUtil;
import com.uade.tpo.almacen.service.UsuarioService;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static class UsuarioProfileDTO {
        public Long id;
        public String username;
        public String email;
        public String nombre;
        public String apellido;
        public String rol;
        public java.time.LocalDateTime fecha_registro;

        public UsuarioProfileDTO(Usuario u) {
            this.id = u.getId();
            this.username = u.getUsername();
            this.email = u.getEmail();
            this.nombre = u.getNombre();
            this.apellido = u.getApellido();
            this.rol = u.getRol().name();
            this.fecha_registro = u.getFechaRegistro();
        }
    }

   @PostMapping("/registro")
public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
    try {
        Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioProfileDTO(nuevoUsuario));
    } catch (IllegalArgumentException e) {
        // Devuelve el mensaje en lugar de null
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}


    // Obtener todos
    @GetMapping
    public ResponseEntity<List<UsuarioProfileDTO>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        List<UsuarioProfileDTO> safeUsuarios = usuarios.stream()
                .map(UsuarioProfileDTO::new)
                .toList();
        return ResponseEntity.ok(safeUsuarios);
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioProfileDTO> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(u -> ResponseEntity.ok(new UsuarioProfileDTO(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Update completo
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioProfileDTO> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            usuario.setId(id);
            Usuario usuarioActualizado = usuarioService.updateUsuario(usuario);
            return ResponseEntity.ok(new UsuarioProfileDTO(usuarioActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Update parcial
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioProfileDTO> patchUsuario(@PathVariable Long id, @RequestBody Usuario usuarioPatch) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(id);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Usuario usuarioExistente = usuarioOpt.get();

            if (usuarioPatch.getUsername() != null)
                usuarioExistente.setUsername(usuarioPatch.getUsername());
            if (usuarioPatch.getEmail() != null)
                usuarioExistente.setEmail(usuarioPatch.getEmail());
            if (usuarioPatch.getPassword() != null)
                usuarioExistente.setPassword(usuarioPatch.getPassword());
            if (usuarioPatch.getNombre() != null)
                usuarioExistente.setNombre(usuarioPatch.getNombre());
            if (usuarioPatch.getApellido() != null)
                usuarioExistente.setApellido(usuarioPatch.getApellido());
            if (usuarioPatch.getRol() != null)
                usuarioExistente.setRol(usuarioPatch.getRol());

            Usuario usuarioActualizado = usuarioService.updateUsuario(usuarioExistente);
            return ResponseEntity.ok(new UsuarioProfileDTO(usuarioActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuarioById(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }

    // Exists username
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        boolean exists = usuarioService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    // Exists email
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = usuarioService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    // Obtener por rol
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioProfileDTO>> getUsuariosByRol(@PathVariable String rol) {
        try {
            Rol rolEnum = Rol.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioService.getUsuariosByRol(rolEnum);
            List<UsuarioProfileDTO> safeUsuarios = usuarios.stream()
                    .map(UsuarioProfileDTO::new)
                    .toList();
            return ResponseEntity.ok(safeUsuarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Perfil autenticado
    @GetMapping("/me")
    public ResponseEntity<UsuarioProfileDTO> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioByUsername(username);
        return usuarioOpt.map(u -> ResponseEntity.ok(new UsuarioProfileDTO(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = loginRequest.getUsername() != null
                ? usuarioService.getUsuarioByUsername(loginRequest.getUsername())
                : usuarioService.getUsuarioByEmail(loginRequest.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol().name());

        UsuarioLoginResponse response = new UsuarioLoginResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().name()
        );

        return ResponseEntity.ok(new LoginJwtResponse(token, response));
    }
}
