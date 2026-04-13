package sau.lpm_v3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/403", "/login", "/register", "/images/**", "/css/**", "/js/**").permitAll()

                        .requestMatchers("/student/add", "/student/delete/**").hasRole("ADMIN")
                        .requestMatchers("/place/add", "/place/update/**").hasRole("ADMIN")

                        .requestMatchers("/student/all", "/student/{id}", "/student/update/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/place/all", "/place/{id}", "/place/delete/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/reservation/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/");
                        })
                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/error/403")
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
