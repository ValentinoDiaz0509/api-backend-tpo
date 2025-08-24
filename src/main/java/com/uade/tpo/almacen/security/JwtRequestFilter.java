package com.uade.tpo.almacen.security;

import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    // Prefijos públicos (no requieren auth)
    private static final List<String> WHITELIST_PREFIXES = List.of(
            "/swagger-ui/",
            "/v3/api-docs/"
    );

    // Rutas exactas públicas
    private static final Set<String> WHITELIST_EXACT = Set.of(
            "/swagger-ui.html",
            "/v3/api-docs",
            "/usuarios/login",
            "/usuarios",
            "/producto",
            "/producto/catalogo",
            "/error"
    );

    public JwtRequestFilter(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (WHITELIST_EXACT.contains(path)) return true;
        return WHITELIST_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception ignored) {
                // token inválido o malformado; dejamos que siga sin auth
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var usuarioOpt = usuarioService.getUsuarioByUsername(username);

            if (usuarioOpt.isPresent() && jwtUtil.isTokenValid(token, username)) {
                Usuario u = usuarioOpt.get();

                String rol = (u.getRol() == null || u.getRol().isBlank())
                        ? "ROLE_USER"
                        : (u.getRol().startsWith("ROLE_") ? u.getRol() : "ROLE_" + u.getRol().toUpperCase());

                var authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));

                var principal = new User(u.getUsername(), u.getPassword(), authorities);
                var authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
