package controller;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Controller {
    Logger logger;

    public Controller(Logger logger) {
        this.logger = Logger.getLogger("logger");
    }

    public Controller(){};

    public void sendMessage(String sourceUser, String targetUser, String message) {
        logger.info(message);
    }
}
