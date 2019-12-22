import sessionHandler.NetworkController;
import properties.Properties;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;

    private static Properties properties;

    private static NetworkController networkController;

    //TODO: better exception
    public static void main(String[] args) {
        configureLogger();
        properties = new Properties();

        networkController = new NetworkController();
        networkController.start();
    }

    private static void configureLogger() {
        try {
            logFileHandler = new FileHandler("logs.log",true);
            logger.addHandler(logFileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            logFileHandler.setFormatter(formatter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
