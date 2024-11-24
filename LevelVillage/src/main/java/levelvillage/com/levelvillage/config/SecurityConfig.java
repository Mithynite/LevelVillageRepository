package levelvillage.com.levelvillage.config;

import levelvillage.com.levelvillage.filter.JWTAuthenticationFilter;
import levelvillage.com.levelvillage.service.UserDetailsServiceImplementation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration // aby fungovaly Beany
@EnableWebSecurity //že v této třídě použijeme security, a že má SB použít tohle
public class SecurityConfig{

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImplementation userDetailsService;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter, UserDetailsServiceImplementation userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF (for API requests)
                .cors().and() // Enable CORS handling in Spring Security TODO Maybe should be deleted afterwards due to the same ip
                .authorizeRequests()
                .requestMatchers("/api/signup", "/api/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().disable(); // Disable session-based authentication

        // Add the JWTAuthenticationFilter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define the AuthenticationManager as a bean explicitly to avoid conflicts.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(httpForm -> {
                    httpForm.loginPage("/").permitAll();
                }).authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/signup").permitAll();
                    registry.anyRequest().authenticated();
                }).build();
        http
                .cors().and() // Enable CORS
                .csrf().disable() // Disable CSRF for API endpoints
                .authorizeRequests()
                .requestMatchers("/api/signup", "/api/login").permitAll() // Allow public access to signup and login
                .anyRequest().authenticated() // All other endpoints require authentication
                .and()
                .httpBasic(); // Basic auth (for development/testing)

        return http.build();
    }*/

    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();
        return http.build();

    }*/
    /**
     * Configure the AuthenticationManagerBuilder to use the custom UserDetailsService and password encoder.
     */

}
