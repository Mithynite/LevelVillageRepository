package levelvillage.com.levelvillage;

import levelvillage.com.levelvillage.config.ConfigManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of the LevelVillage application.
 *
 * This class is annotated with {@link SpringBootApplication} which enables auto-configuration, component scanning,
 * and other features provided by Spring Boot.
 *
 * @author Jakub Hofman
 */
@SpringBootApplication
public class LevelVillageApplication {

    /**
     * The main method that starts the application.
     *
     * @param args Command-line arguments passed to the application.
     * @throws Exception If an error occurs during the initialization of the configuration or while running the application.
     */
    public static void main(String[] args) throws Exception {
        ConfigManager.initializeConfig();
        SpringApplication.run(LevelVillageApplication.class, args);
    }

}
