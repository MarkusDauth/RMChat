package controller;

import model.NewUser;

import java.util.logging.Logger;

public class Controller {
    private final Logger logger;
    private final Properties properties;


    //TODO wird gebraucht für View??? Eventuell Properties Konstruktor in main stecken
    public Controller() {
        this.logger = Logger.getLogger("logger");
        this.properties = new Properties();
    }

    public void createNewUser(NewUser newUser){
        logger.info("Creating new user: "+newUser.getUserName());

    }

}
