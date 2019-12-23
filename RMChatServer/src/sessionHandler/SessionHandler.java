package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Class for handling Logins and the resulting Session
 */
class SessionHandler {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    private static final List<UserSession> sessions = new LinkedList<>();



    /**
     * Checks if login credentials are correct and creates new session
     * @param tcpSend
     * @param tcpReceive
     * @throws IOException
     */
    public static void login(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String username = tcpReceive.readNextString();
        String password = tcpReceive.readNextString();
        logger.info("Trying to login: " + username);

        UserSession session = new UserSession(tcpSend, tcpReceive, username, password);
        if(session.setupSession()){
            sessions.add(session);
            tcpSend.add("OKLOG");
            tcpSend.add(Long.toString(session.getSessionId()));
            tcpSend.send();
            logger.info("Successfull Login: " + username +". Current Logged in Users: "+sessions.size());
        }
    }
}
