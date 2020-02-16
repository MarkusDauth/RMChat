package session;

import database.DatabaseInterface;
import database.TextfileDatabase;
import properties.Properties;
import network.TcpReceive;
import network.TcpSend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;


/**
 * Class for handling Logins and the resulting Session
 */
public class SessionHandler {
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
    public static void login(Socket socket, TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String username = tcpReceive.readNextString();
        String password = tcpReceive.readNextString();
        int clientPort = 0;
        try{
            clientPort = Integer.parseInt(tcpReceive.readNextString());
            logger.info("Trying to login: " + username);

            Optional<UserSession> session = getSession(username);
            if (session.isPresent()) {
                tcpSend.sendError("AlreadyLoggedIn");
                logger.info("User " + username + " already logged in ");
            } else {
                UserSession newSession = new UserSession(username, password, socket.getInetAddress(), clientPort);
                if (newSession.setupSession(tcpSend)) {
                    //Session is online
                    sessions.add(newSession);
                    newSession.finishLogin(tcpSend);
                    logger.info("Current Logged in Users: " + sessions.size());
                }
            }
        }catch (Exception e){
            logger.info("Invalid Port from client");
        }
    }

    /**
     * get Session with corresponding username
     *
     * @param username
     * @return is null if no session is found, which means the user is offline
     */
    private static Optional<UserSession> getSession(String username) {
        return sessions.stream().filter(s -> s.getUsername().equals(username)).findFirst();
    }

    public static void checkAlives() {
        logger.fine("Checking " + sessions.size() + " alive sessions");
        for (int i = 0; i < sessions.size(); i++) {
            UserSession session = sessions.get(i);
            if (!session.isAlive()) {
                sessions.remove(i);
            }
        }
    }

    /**
     * Updates the session which sent an alive Message.
     * Responds to client with OKALV, followed by a list of friends. Example:
     * OKALV FriendA 0 FriendB 1 FriendC 1
     * 0 = offline, 1 = online
     *
     * @param tcpSend
     * @param tcpReceive
     */
    public static void updateSessionAlive(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        String sessionId = tcpReceive.readNextString();

        Optional<UserSession> session = sessions.stream().
                filter(s -> s.getSessionId().equals(sessionId)).
                findAny();

        if (session.isPresent()) {
            logger.fine("User " + session.get().getUsername() + " is alive!");
            session.get().updateLastAliveDate();

            sendOKALV(tcpSend, session.get());
        } else {
            logger.info("Someone wants to be alive, but is not logged in");
            tcpSend.sendError("UserNotLoggedIn");
        }
    }

    /**
     * Also sends friend requests
     *
     * @param tcpSend
     * @param session
     * @throws IOException
     */
    private static void sendOKALV(TcpSend tcpSend, UserSession session) throws IOException {
        tcpSend.add("OKALV");

        Set<String> friends = session.getFriends();
        for (String friend : friends) {
            tcpSend.add(friend);
            if (sessions.stream().filter(s -> s.getUsername().equals(friend)).findAny().isPresent()) {
                tcpSend.add("1");
            } else {
                tcpSend.add("0");
            }
            logger.fine("Friend " + friend);
        }

        //Mark end of friend
        tcpSend.add("");
        tcpSend.send();
    }

    private static Optional<UserSession> findSession(String sessionId) {
        Optional<UserSession> session = sessions.stream().
                filter(s -> s.getSessionId().equals(sessionId)).
                findFirst();
        return session;
    }

    /**
     * A client wants to send a message from another client
     *
     * @param senderTcpSend
     * @param senderTcpReceive
     * @throws IOException
     */
    public static void sendMessage(TcpSend senderTcpSend, TcpReceive senderTcpReceive) throws IOException {
        //1. Parameter
        String chatMessageSenderSessionId = senderTcpReceive.readNextString();

        Optional<UserSession> chatMessageSenderSession = findSession(chatMessageSenderSessionId);
        if (chatMessageSenderSession.isPresent()) {
            chatMessageSenderSession.get().updateLastAliveDate();

            //2. Parameter
            String chatMessageRecipient = senderTcpReceive.readNextString();

            //3. Parameter
            String message = senderTcpReceive.readNextString();

            logger.info(chatMessageSenderSession.get().getUsername() + " wants to send a message to " + chatMessageRecipient);

            //Check if recipient is logged in
            Optional<UserSession> chatMessageRecipientSession = getSession(chatMessageRecipient);
            int messageMinLength = Properties.getInt("message.minLength");
            int messageMaxLength = Properties.getInt("message.maxLength");


            if (!chatMessageSenderSession.get().hasFriend(chatMessageRecipient)) {
                logger.info("Recipient is not a friend");
                senderTcpSend.sendError("RecipientNotAFriend");
            } else if (!chatMessageRecipientSession.isPresent()) {
                logger.info("Recipient is offline");
                senderTcpSend.sendError("RecipientNotLoggedIn");
            } else if (message.length() < messageMinLength) {
                logger.info("Message too short");
                senderTcpSend.sendError("MessageTooShort");
            } else if (message.length() > messageMaxLength) {
                logger.info("Message Too Long");
                senderTcpSend.sendError("MessageTooLong");
            } else {
                forwardMessage(
                        senderTcpSend,
                        chatMessageSenderSession.get(),
                        chatMessageRecipientSession.get(),
                        message);
            }
        } else {
            logger.info("Someone wants to send a message, but is not logged in");
            senderTcpSend.sendError("UserNotLoggedIn");
        }
    }

    /**
     * Sends RCMSG to the receiving client of the chat message
     */
    private static void forwardMessage(TcpSend senderTcpSend, UserSession senderSession,
                                       UserSession recipientSession,
                                       String message) throws IOException {

        logger.info("Sending message from " + senderSession.getUsername() + " to " + recipientSession.getUsername());
        //Stops possible problems with delayed messages
        recipientSession.updateLastAliveDate();

        //Net socket with recieving client
        Socket socket = createSocket(recipientSession.getInetAddress(), recipientSession.getClientPort());
        TcpSend recipeintTcpSend = new TcpSend(socket.getOutputStream());
        TcpReceive recipientTcpReceive = new TcpReceive(socket.getInputStream());

        //Send SENDMSG to Recipient
        recipeintTcpSend.add("RECMSG");
        //Send THIS sessions username, which is the SENDER of the chat message
        recipeintTcpSend.add(senderSession.getUsername());
        recipeintTcpSend.add(message);
        recipeintTcpSend.send();

        receiveOKREC(senderTcpSend, recipientTcpReceive, recipientSession);
    }

    private static void receiveOKREC(TcpSend senderTcpSend, TcpReceive recipientTcpReceive, UserSession recipientSession) throws IOException {
        recipientTcpReceive.receive();
        String code = recipientTcpReceive.readNextString();
        String sessionId = recipientTcpReceive.readNextString();
        //Check if the receiver is correct. Let timeout happen, if not.
        if (!code.equals("OKREC")) {
            logger.info("Wrong Code");
        } else if (!sessionId.equals(recipientSession.getSessionId())) {
            logger.info("Error: wrong SessionId");
        } else {
            sendOKSEN(senderTcpSend);
        }

    }

    private static void sendOKSEN(TcpSend senderTcpSend) throws IOException {
        logger.info("Sending OKSEN");
        senderTcpSend.add("OKSEN");
        senderTcpSend.send();
    }

    static Socket createSocket(InetAddress inetAddress, int clientPort) {
        Socket socket = null;
        try {
            socket = new Socket(inetAddress, clientPort);
            int timeout = Properties.getInt("tcp.clientSessionTimeout.seconds");
            socket.setSoTimeout(timeout * 1000);
            logger.info("Connected to Client: " + socket);
        } catch (SocketException e) {
            logger.info("Error: " + e.getMessage());
        } catch (IOException e) {
            logger.info("Error: " + e.getMessage());
        }

        return socket;
    }

    public static void addFriend(TcpSend tcpSend, TcpReceive tcpReceive) throws IOException {
        //1. Parameter
        String sessionId = tcpReceive.readNextString();

        Optional<UserSession> session = findSession(sessionId);
        if (session.isPresent()) {
            session.get().updateLastAliveDate();

            //2. Parameter
            String newFriend = tcpReceive.readNextString();

            logger.info(session.get().getUsername() + " wants to be friends with " + newFriend);

            if (session.get().hasFriend(newFriend)) {
                logger.info("Friend Request failed: already friends");
                tcpSend.sendError("AlreadyFriends");
            } else if (!database.usernameIsPresent(newFriend)) {
                logger.info("Friend Request from " + session.get().getUsername() + "failed: Friend " + newFriend + " does not exist.");
                tcpSend.sendError("FriendDoesNotExist");
            } else {
                session.get().addFriend(newFriend);
                sendFriendRequest(tcpSend, session.get().getUsername(), newFriend);
            }
        } else {
            logger.info("Someone wants to send a message, but is not logged in");
            tcpSend.sendError("UserNotLoggedIn");
        }
    }

    private static void sendFriendRequest(TcpSend requesterTcpSend, String requester, String newFriend) throws IOException {
        Optional<UserSession> newFriendSession = getSession(newFriend);

        if (!newFriendSession.isPresent()) {
            logger.info("That new friend is offline");
            requesterTcpSend.sendError("FriendNotOnline");
        } else {
            logger.info("Sending friend request to " + newFriend);

            //Net socket with recieving client
            Socket socket = createSocket(newFriendSession.get().getInetAddress(), newFriendSession.get().getClientPort());
            int timeout = Properties.getInt("tcp.friendRequestTimeout.seconds");
            socket.setSoTimeout(60 * 1000);

            TcpSend recipeintTcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive recipientTcpReceive = new TcpReceive(socket.getInputStream());

            recipeintTcpSend.add("FRIENDREQ");
            //Send THIS sessions username, which is the SENDER of the chat message
            recipeintTcpSend.add(requester);
            recipeintTcpSend.send();

            receiveOKFRIENDREQ(requesterTcpSend, recipientTcpReceive, newFriendSession.get(), requester);
        }
    }

    private static void receiveOKFRIENDREQ(TcpSend requesterTcpSend, TcpReceive recipientTcpReceive, UserSession newFriendSession, String requester) throws IOException {
        recipientTcpReceive.receive();
        //Loading 3 Parameters
        String code = recipientTcpReceive.readNextString();
        String sessionId = recipientTcpReceive.readNextString();
        String acceptance = recipientTcpReceive.readNextString();

        logger.info("Received: " + code + " " + sessionId + " " + acceptance);

        //Check if the receiver is correct. Let timeout happen, if not.
        if (code.equals("OKFRIENDREQ") && sessionId.equals(newFriendSession.getSessionId())) {
            if (acceptance.equals("1"))
                saveNewFriendship(requesterTcpSend, newFriendSession.getUsername(), requester);
            else
                logger.info("Friend request denied");
        }
    }

    private static void saveNewFriendship(TcpSend requesterTcpSend, String requester, String newFriend) throws IOException {
        if (database.addFriend(requester, newFriend)) {
            logger.info(requester + " is now friends with " + newFriend);

            requesterTcpSend.add("OKFRIEND");
            requesterTcpSend.send();
        } else {
            requesterTcpSend.sendError("Unexpected");
            logger.info("Unexpected");
        }
    }
}
