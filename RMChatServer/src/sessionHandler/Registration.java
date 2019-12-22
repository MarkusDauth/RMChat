package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import properties.Properties;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.util.logging.Logger;

public class Registration {
    private static Logger logger = Logger.getLogger("logger");
    private static DatabaseInterface database = TextfileDatabase.getInstance();

    public static void registerUser(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String username = tcpReceive.readNextString();
        String password = tcpReceive.readNextString();
        logger.info("Registration: " + username + password);
        //database.registerUser();

        tcpSend.add("OK");
        tcpSend.send();
    }

    private static boolean validatePassword(String password){
        if(password.length() < Properties.getInt("password.minLength"))
            return false;
        if(password.length() > Properties.getInt("password.maxLength"))
            return false;
        return true;
    }

    private boolean validateUsername(String username){
        return false;
    }
}
