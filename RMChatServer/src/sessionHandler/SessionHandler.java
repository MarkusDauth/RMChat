package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.net.Socket;
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
     *
     * @param socket
     * @param tcpSend
     * @param tcpReceive
     * @throws IOException
     */
    public static void login(Socket socket, TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        UserSession session = new UserSession(socket, tcpSend, tcpReceive);

        if(!loggedIn(session) && session.setupSession()){
            sessions.add(session);
            session.finishLogin();
            logger.info("Current Logged in Users: "+ sessions.size());
        }
    }

    private static boolean loggedIn(UserSession session) {
        return sessions.stream().filter(s -> s.getUsername().equals(session.getUsername())).findFirst().isPresent();
    }
}
