package levelvillage.com.levelvillage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is responsible for configuring Cross-Origin Resource Sharing (CORS) for the Spring Boot application.
 * It implements the {@link WebMvcConfigurer} interface to override the {@link #addCorsMappings(CorsRegistry)} method.
 *
 * @author Jakub Hofman
 */
@Configuration
public class CORSConfig implements WebMvcConfigurer {

    /**
     * Overrides the default CORS configuration.
     *
     * @param registry The {@link CorsRegistry} to customize CORS settings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints
                .allowedOrigins("http://localhost:5173") // Adjust this if necessary
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

