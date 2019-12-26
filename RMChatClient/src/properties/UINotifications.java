package properties;

import java.io.FileInputStream;
import java.io.IOException;

public class UINotifications {
    private static java.util.Properties notification = new java.util.Properties();

    static{
        String resourceName = "properties/userInterface.notifications";
        try{
            notification.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String key) {
        return notification.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(notification.getProperty(key));
    }
}
