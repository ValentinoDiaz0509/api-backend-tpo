package com.uade.tpo.almacen.security;

import com.uade.tpo.almacen.entity.Usuario;
import com.uade.tpo.almacen.service.impl.UsuarioService;
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

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    // Endpoints públicos (no requieren auth)
    private static final List<String> WHITELIST = List.of(
            "/auth/login",
            "/auth/register",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/producto",           
            "/producto/catalogo"
    );

    public JwtRequestFilter(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return WHITELIST.stream().anyMatch(path::startsWith);
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
