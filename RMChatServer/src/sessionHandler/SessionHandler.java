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

    /**
     * get Session with corresponding username
     * @param username
     * @return is null if no session is found, which means the user is offline
     */
    private static Optional<UserSession> getSession(String username) {
        return sessions.stream().filter(s -> s.getUsername().equals(username)).findFirst();
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
            logger.info("User " + session.get().getUsername() + " is alive!");
            session.get().updateLastAliveDate();

            tcpSend.add("OKALV");
            tcpSend.send();
        } else {
            logger.info("Someone wants to be alive, but is not logged in");
            tcpSend.sendError("UserNotLoggedIn");
        }
    }

    private static Optional<UserSession> findSession(String sessionId) {
        Optional<UserSession> session = sessions.stream().
                filter(s -> s.getSessionId().equals(sessionId)).
                findFirst();
        return session;
    }

    /**
     * A client wants to send a message from another client
     * @param tcpSend
     * @param tcpReceive
     * @throws IOException
     */
    public static void sendMessage(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        //1. Parameter
        String chatMessageSenderSessionId = tcpReceive.readNextString();

        Optional<UserSession> chatMessageSenderSession = findSession(chatMessageSenderSessionId);
        if (chatMessageSenderSession.isPresent()) {
            chatMessageSenderSession.get().updateLastAliveDate();

            //2. Parameter
            String chatMessageRecipient = tcpReceive.readNextString();
            Optional<UserSession> chatMessageRecipientSession = getSession(chatMessageRecipient);

            //3. Parameter
            String message = tcpReceive.readNextString();

            chatMessageSenderSession.get().forwardMessage(chatMessageRecipientSession, message);
        } else {
            logger.info("Someone wants to send a message, but is not logged in");
            tcpSend.sendError("UserNotLoggedIn");
        }
    }

    /**
     * Handles the answer from the "chat message recipient" when he successfully received a chat message.
     * Sends OKSEN to the "chat message sender"
     * @param tcpSend
     * @param tcpReceive
     */
    public static void messageSuccessfullyReceived(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        /**
         * TCP Package has the following Parameters:
         * OKREC
         * sessionId from Recipient
         * username of sender
         */

        //1. Parameter after Code
        String chatMessageRecipientSessionId = tcpReceive.readNextString();

        Optional<UserSession> chatMessageRecipientSession = findSession(chatMessageRecipientSessionId);
        if (chatMessageRecipientSession.isPresent()) {
            //Recipient of chat message is logged in
            chatMessageRecipientSession.get().updateLastAliveDate();

            //Send OKSEN to chatMessageRSender
            //2. Parameter
            String chatMessageSender = tcpReceive.readNextString();
            Optional<UserSession> chatMessageSenderSession = getSession(chatMessageSender);

            chatMessageSenderSession.get().sendOKSEN();
        } else {
            logger.info("Someone wants to send OKREC, but is not logged in");
            tcpSend.sendError("UserNotLoggedIn");
        }
    }
}
