package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import properties.Properties;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.util.logging.Logger;

class Registration {
    private static final Logger logger = Logger.getLogger("logger");
    private static final DatabaseInterface database = TextfileDatabase.getInstance();

    public static void registerUser(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String username = tcpReceive.readNextString();
        String password = tcpReceive.readNextString();
        logger.info("Registration: " + username);

        if(! validUsername(username)){
            tcpSend.sendError("InvalidUsername");
            logger.info("InvalidUsername");
        }
        else if(! validPassword(password)) {
            tcpSend.sendError("InvalidPassword");
            logger.info("InvalidPassword");
        }
        else if( database.usernameIsPresent(username)) {
            tcpSend.sendError("UsernameTaken");
            logger.info("UsernameTaken");
        }

        else if (database.registerUser(username, password)) {
            tcpSend.add("OKREG");
            tcpSend.send();
            logger.info("OKREG");

        } else {
            tcpSend.sendError("Unexpected");
            logger.info("Unexpected");
        }
    }

    private static boolean validPassword(String password) {
        if (password.length() < Properties.getInt("password.minLength"))
            return false;
        else return password.length() <= Properties.getInt("password.maxLength");
    }

    private static boolean validUsername(String username) {
        if (username.length() < Properties.getInt("username.minLength"))
            return false;
        else return username.length() <= Properties.getInt("username.maxLength");

    }
}
