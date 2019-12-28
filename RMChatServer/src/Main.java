
import network.NetworkController;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class Main {
    private static final Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;

    public static void main(String[] args) {
        //TODO receive message: check for max and min size

        configureLogger();
        logger.info("START");



        NetworkController networkController = new NetworkController();
        networkController.start();
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
