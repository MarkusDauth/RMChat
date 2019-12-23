package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.util.logging.Logger;


/**
 * Class for handling Logins and the resulting Session
 */
class SessionHandler {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    /*
    Checks if login credentials are correct and creates new session
     */
    public static void login(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String username = tcpReceive.readNextString();
        String password = tcpReceive.readNextString();
        logger.info("Trying to login: " + username);

        UserSession session = new UserSession();
        session.setUsername(username);
        session.setPassword(password);
        session.setTcpSend(tcpSend);
        session.setTcpReceive(tcpReceive);

        if (session.validateCredentials()) {
            tcpSend.add("OKLOG");
            tcpSend.send();
            logger.info("Successfull Login: " + username);
        }
    }
}
