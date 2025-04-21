package levelvillage.com.levelvillage.config;

import levelvillage.com.levelvillage.filter.JWTAuthenticationFilter;
import levelvillage.com.levelvillage.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class is responsible for configuring Spring Security for the LevelVillage application.
 * It sets up authentication, authorization, and CSRF protection.
 *
 * @author Jakub Hofman
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    /**
     * Constructor for SecurityConfig class.
     *
     * @param jwtAuthenticationFilter The JWTAuthenticationFilter instance to be used for authentication.
     * @param userService The UserService instance to handle user-related operations.
     */
    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter, UserService userService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http The HttpSecurity instance to configure security settings.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF (for API requests)
                .cors().and() // Enable CORS handling in Spring Security
                .authorizeRequests()
                .requestMatchers("/api/signup", "/api/login").permitAll()  // Allow signup and login without authentication
                .requestMatchers("/api/validate/**").permitAll()  // Allow validate endpoint without authentication
                .anyRequest().authenticated()  // Require authentication for other endpoints
                .and()
                .sessionManagement().disable();  // Disable session-based authentication

        // Add the JWTAuthenticationFilter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the password encoder for user passwords.
     *
     * @return The configured PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the AuthenticationManager as a bean explicitly to avoid conflicts.
     *
     * @param configuration The AuthenticationConfiguration instance to retrieve the AuthenticationManager.
     * @return The configured AuthenticationManager.
     * @throws Exception If an error occurs during AuthenticationManager configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
