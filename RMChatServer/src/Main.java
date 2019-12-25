
import session.NetworkController;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class Main {
    private static final Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;

    //TODO: better exception
    public static void main(String[] args) {
        logger.info("START");

        configureLogger();

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
