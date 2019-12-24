package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


/**
 * Class for handling Logins and the resulting Session
 */
class SessionHandler {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    /**
     * Has all UserSession objects, that are logged in
     */
    private static final List<UserSession> sessions = new LinkedList<>();

    /**
     * Checks if login credentials are correct and creates new session
     *
     * @param tcpSend
     * @param tcpReceive
     * @throws IOException
     */
    public static void login(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        UserSession session = new UserSession(tcpSend, tcpReceive);

        if (!isLoggedIn(session, tcpSend) && session.setupSession()) {
            sessions.add(session);
            session.finishLogin();
            logger.info("Current Logged in Users: " + sessions.size());
        }
    }

    private static boolean isLoggedIn(UserSession session, TcpSend tcpSend) throws IOException {
        boolean loggedIn = sessions.stream().filter(s -> s.getUsername().equals(session.getUsername())).findFirst().isPresent();
        if (loggedIn) {
            tcpSend.sendError("AlreadyLoggedIn");
            logger.info("User " + session.getUsername() + " already logged in ");
            return true;
        }
        return false;
    }

    public static void checkAlives() {
        logger.info("Checking " + sessions.size() + " alive sessions");
        for (int i = 0; i < sessions.size(); i++) {
            UserSession session = sessions.get(i);
            if (!session.isAlive()) {
                sessions.remove(i);
            }
        }
    }

    /**
     * Updates the session which sent an alive Message
     *
     * @param tcpSend
     * @param tcpReceive
     */
    public static void updateSessionAlive(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String sessionId = tcpReceive.readNextString();

        Optional<UserSession> session = sessions.stream().
                filter(s -> s.getSessionId().equals(sessionId)).
                findFirst();

        if (session.isPresent()) {
            logger.info("User "+ session.get().getUsername()+" is alive!");
            session.get().updateLastAliveDate();
            tcpSend.add("OKALV");
            tcpSend.send();
        } else {
            logger.info("User "+ session.get().getUsername()+" wants to be alive, but is not online");
            tcpSend.sendError("UserNotLoggedIn");
        }
    }
}
