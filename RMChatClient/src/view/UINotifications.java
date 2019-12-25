package view;

import java.io.FileInputStream;
import java.io.IOException;

public class UINotifications {
    private static java.util.Properties notification = new java.util.Properties();

    static {
        try {
            notification.load(new FileInputStream("resources/userInterface.notifications"));
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
