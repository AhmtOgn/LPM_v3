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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF'i kapatıyoruz ki POST/Upload işlemleri engellenmesin
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // "anyRequest().permitAll()" diyerek tüm kapıları açıyoruz
                        .anyRequest().permitAll()
                )

                // Form login ve logout'u şimdilik yorum satırına alabilirsin
                /*.formLogin(form -> form.permitAll())*/

                // H2-Console veya diğer frame yapıları için (gerekirse)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Yetkilendirme Kuralları (Authorization)
                .authorizeHttpRequests(auth -> auth
                        // Statik kaynaklar ve giriş/kayıt sayfaları herkese açık
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()

                        // Admin'e özel kritik işlemler (Ekleme, Silme, Güncelleme)
                        .requestMatchers("/student/add/**", "/student/delete/**", "/student/update/**").hasRole("ADMIN")
                        .requestMatchers("/place/add/**", "/place/delete/**", "/place/update/**").hasRole("ADMIN")
                        .requestMatchers("/reservation/delete/**").hasRole("ADMIN")

                        // Geri kalan her şey (Görüntüleme vb.) giriş yapmış kullanıcıya açık
                        .anyRequest().authenticated()
                )

                // 2. Giriş (Login) Yapılandırması
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )


                // 3. Çıkış (Logout) Yapılandırması
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // 4. Yetkisiz Erişim Hatası (403 Forbidden)
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/403")
                )

                // 5. CORS Politikası (Ödev gereği belirtilmeli)
                .cors(cors -> cors.configure(http));

        return http.build();
    }
     */
}