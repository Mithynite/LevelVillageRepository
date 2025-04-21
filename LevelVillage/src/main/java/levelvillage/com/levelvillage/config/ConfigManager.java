package levelvillage.com.levelvillage.config;

import java.util.Properties;

public class ConfigManager {
    public static String configFilePath = "application.properties";

    public static String jwtSecret;
    public static int maxPostTitleCharLength;
    public static int maxPostDescriptionCharLength;
    public static int maxUsernameCharLength;
    public static int maxUserBioCharLength;

    /**
     * Initializes the configuration settings by loading values from a properties file.
     * This method reads various configuration parameters such as server port, database credentials,
     * timeouts, and account-related settings from the specified properties file.
     *
     * @throws Exception If there's an error loading the properties file or parsing the values.
     */
    public static void initializeConfig() throws Exception {
        Properties props = new Properties();
        props.load(ConfigManager.class.getClassLoader().getResourceAsStream(configFilePath));

        jwtSecret = props.getProperty("jwt.secret");
        maxPostTitleCharLength = Integer.parseInt(props.getProperty("post.title.char.length"));
        maxPostDescriptionCharLength = Integer.parseInt(props.getProperty("post.description.char.length"));
        maxUsernameCharLength = Integer.parseInt(props.getProperty("user.username.char.length"));
        maxUserBioCharLength = Integer.parseInt(props.getProperty("user.bio.char.length"));
    }

}
