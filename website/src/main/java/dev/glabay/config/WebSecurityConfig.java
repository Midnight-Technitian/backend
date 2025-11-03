package dev.glabay.config;

import dev.glabay.feaures.users.CustomUserDetailsService;
import dev.glabay.feaures.users.UserProfileRepository;
import dev.glabay.feaures.roles.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserProfileRepository userProfileRepository;
    private final UserRoleService userRoleService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**")
                .permitAll()
                .requestMatchers(
                    "/",
                    "/home",
                    "/index",
                    "/error",
                    "/auth/login",
                    "/auth/register",
                    "/css/**",
                    "/webjars/**"
                ).permitAll()
                // User-Relate (requires a logged-in user to have a ROLE_USER to access)
                .requestMatchers("/dashboard/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/perform-login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // If you have APIs
            );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new CustomUserDetailsService(userProfileRepository,  userRoleService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(getUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
