package properties;

import java.io.FileInputStream;
import java.io.IOException;

public class UINotifications {
    private static java.util.Properties notification = new java.util.Properties();
    private final static String LANGUAGE;

    static{
        LANGUAGE = Properties.getString("LANGUAGE");
        String resourceName = "properties/userInterface.notifications";
        try{
            notification.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return notification.getProperty(LANGUAGE+key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(notification.getProperty(LANGUAGE+key));
    }
}
