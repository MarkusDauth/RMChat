import Network.NetworkController;
import Network.Properties;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static Logger logger;
    private static FileHandler logFileHandler;

    private static Properties properties;

    private static NetworkController networkController;

    //TODO: better exception
    public static void main(String[] args) throws IOException {
        configureLogger();
        properties = new Properties();

        networkController = new NetworkController(logger, properties);
        networkController.tcpTest();
    }

    private static void configureLogger() {
        logger = Logger.getLogger("logger");

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
