package session;

import database.DatabaseInterface;
import database.TextfileDatabase;
import properties.Properties;
import network.TcpSend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Objects of this class represent an active user session (user is online)
 */
public class UserSession {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    private String username;
    private String password;
    private Set<String> friends;

    private String sessionId;
    private InetAddress inetAddress;
    private int clientPort;

    private LocalTime lastAliveDate;

    public UserSession(String username, String password, InetAddress inetAddress, int clientPort) {
        this.username = username;
        this.password = password;
        this.inetAddress = inetAddress;
        this.clientPort = clientPort;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public Set<String> getFriends() { return Collections.unmodifiableSet(friends) ;};

    /**
     * Checks if user login information is correct
     * @param tcpSend
     * @return true, if user information is correct and session is "online" afterwards. Otherwise return false and session should be discarded
     * @throws IOException
     */
    public boolean setupSession(TcpSend tcpSend) throws IOException {
        if (!validateCredentials(tcpSend)) {
            return false;
        }
        //Session is online
        friends = database.getFriends(username);
        createSessionId();
        updateLastAliveDate();
        return true;
    }

    /**
     * Überprüft, ob Logindaten korrekt sind.
     * @param tcpSend
     * @return
     * @throws IOException
     */
    public boolean validateCredentials(TcpSend tcpSend) throws IOException {
        if (!database.usernameIsPresent(username)) {
            tcpSend.sendError("WrongUsername");
            logger.info("WrongUsername");
            return false;
        } else if (!database.checkPassword(username, password)) {
            tcpSend.sendError("WrongPassword");
            logger.info("WrongPassword");
            return false;
        }
        return true;
    }

    public void updateLastAliveDate() {
        lastAliveDate = LocalTime.now();
    }

    private void createSessionId(){
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(username);
        sb.append(rand.nextInt(1000));
        sessionId = sb.toString();
    }

    public void finishLogin(TcpSend tcpSend) throws IOException {
        tcpSend.add("OKLOG");
        tcpSend.add(sessionId);
        tcpSend.send();
        logger.info("Successfull Login: " + username);
    }

    /**
     * Checks if last KeepAlive message is older than timeout timeframe
     * @return
     */
    public boolean isAlive(){
        int timeoutSeconds = Properties.getInt("server.keepAliveTimeout.seconds");
        long timePassed = SECONDS.between(lastAliveDate,LocalTime.now());
        logger.fine(username + " last sign of life was "+timePassed+" seconds ago.");
        return (timePassed < timeoutSeconds);
    }

    public boolean hasFriend(String username){
        return friends.contains(username);
    }

    public void addFriend(String newFriend){
        friends = database.getFriends(username);
    }

    public int getClientPort() {
        return clientPort;
    }
}
