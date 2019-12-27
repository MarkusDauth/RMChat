import controller.network.NetworkController;
import view.Views;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;

    public static void main(String[] args) {
        configureLogger();
        logger.info("START");

        //MVC setup
        NetworkController networkController = new NetworkController();
        //Launch
        Views.launchApplication(networkController);

        logFileHandler.close();
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
