package properties;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Klasse für Zugriff auf config.properties
 * Beispiel aufruf:
 * int length = Properties.getInt("username.maxLength");
 */
public class Properties {
    private static java.util.Properties properties = new java.util.Properties();

    static {
        try {
            properties.load(new FileInputStream("resources/config.properties"));
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
