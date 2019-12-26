package properties;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Klasse für Zugriff auf config.properties
 * Beispiel aufruf:
 * int length = properties.getInt("username.maxLength");
 */
public class Properties {
    private static java.util.Properties properties = new java.util.Properties();

    static{
        String resourceName = "properties/config.properties";
        try{
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
