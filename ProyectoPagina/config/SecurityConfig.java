package com.ProyectoPaginaWeb.ProyectoPagina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ✅ Recursos estáticos públicos
                .requestMatchers(
                    "/css/**", "/CSS/**", "/js/**", "/JS/**", 
                    "/img/**", "/images/**", "/video/**", "/documento/**", 
                    "/Documento/**", "/fonts/**", "/webjars/**", 
                    "/favicon.ico", "/error"
                ).permitAll()

                // ✅ Páginas públicas y endpoints de autenticación
                .requestMatchers(
                    "/", "/principal", "/ventas", "/productos", "/ofertas",
                    "/info", "/zapato**", "/login", "/registro", "/formulario",
                    "/recuperacion/**", "/recuperar-contrasena", "/codigo-recuperacion",
                    "/api/auth/**"  // ✅ Todos los endpoints de auth
                ).permitAll()

                // 🔒 Rutas de administración - SOLO ADMINISTRADORES
                .requestMatchers("/admin/**").hasRole("Administrador")

                // 🔒 Rutas que requieren autenticación (usuarios normales)
                .requestMatchers(
                    "/carrito/**", "/perfil/**", "/mis-pedidos/**",
                    "/checkout/**", "/pago/**"
                ).authenticated()

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // ✅ Configuración de login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/principal", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // ✅ Configuración de logout tradicional
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/principal?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "jwtToken")
                .permitAll()
            )

            // ✅ Configuración de sesiones - Compatibilidad con JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )

            // ✅ Agregar filtro JWT antes del filtro de autenticación por usuario/contraseña
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // ✅ Manejo de excepciones
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/login?denied=true")
            )

            // ⚠️ Desactiva CSRF solo para desarrollo (considera habilitarlo en producción)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}