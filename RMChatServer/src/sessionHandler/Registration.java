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
    private static DatabaseInterface database = new TextfileDatabase();

    public static void registerUser(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String username = tcpReceive.readString();
        String password = tcpReceive.readString();
        logger.info("Registration: " + username);
        database.registerUser(username, password);

        tcpSend.add("OK");
        tcpSend.send();
    }

    private boolean checkPassword(String password){
        if(password.length() < Properties.getInt("username.maxLength"))
    }

    private boolean checkUsername(String username){

    }
}
