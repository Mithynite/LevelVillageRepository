package levelvillage.com.levelvillage.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS to all /api endpoints
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173") // TODO Change this later
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow credentials (like cookies) if necessary
    }
}
