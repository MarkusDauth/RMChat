
import network.NetworkController;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class Main {
    private static final Logger logger = Logger.getLogger("logger");
    private static FileHandler logFileHandler;

    public static void main(String[] args) {

        if(args.length > 0 && args[0].contains("-v")) {
            configureLogger(true);
            logger.severe("START");
            logger.severe("Logger set to verbose");
        }
        else {
            configureLogger(false);
            logger.severe("START");
            logger.severe("Logger set to non verbose");
        }

        NetworkController networkController = new NetworkController();
        networkController.start();
        logFileHandler.close();
    }

    private static void configureLogger(boolean verbose) {
        try {
            if(!verbose){
                logger.setLevel(Level.SEVERE);
            }

            logFileHandler = new FileHandler("logs.log",true);
            logFileHandler.setLevel(Level.FINE);
            logger.addHandler(logFileHandler);

            SimpleFormatter formatter = new SimpleFormatter();
            logFileHandler.setFormatter(formatter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
