package controller;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Klasse für Zugriff auf config.properties
 * Beispiel aufruf:
 * int length = properties.getInt("username.maxLength");
 */
public class Properties {
    java.util.Properties properties;

    public Properties(){
        properties = new java.util.Properties();
        try {
            properties.load(new FileInputStream("resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key)
    {
        return properties.getProperty(key);
    }

    public int getInt(String key)
    {
        return Integer.parseInt(properties.getProperty(key));
    }
}
