package levelvillage.com.levelvillage;

import levelvillage.com.levelvillage.config.ConfigManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LevelVillageApplication {

	public static void main(String[] args) throws Exception {
		ConfigManager.initializeConfig();
		SpringApplication.run(LevelVillageApplication.class, args);
	}

}
