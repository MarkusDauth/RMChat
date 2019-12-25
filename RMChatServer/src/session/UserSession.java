package session;

import database.DatabaseInterface;
import database.TextfileDatabase;
import properties.Properties;
import session.tcp.TcpSend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.util.logging.Logger;

public class UserSession {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    private String username;
    private String password;

    private String sessionId;
    private InetAddress inetAddress;

    private LocalTime lastAliveDate;

    public UserSession(String username, String password, InetAddress inetAddress) {
        this.username = username;
        this.password = password;
        this.inetAddress = inetAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public UserSession(Socket socket) {
        socket.getInetAddress();
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

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

    public boolean setupSession(TcpSend tcpSend) throws IOException {
        if (!validateCredentials(tcpSend)) {
            return false;
        }
        createSessionId();
        updateLastAliveDate();
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
        int timeoutSeconds = Properties.getInt("tcp.clientSessionTimeout");
        long timePassed = SECONDS.between(lastAliveDate,LocalTime.now());
        logger.fine(username + " last sign of life was "+timePassed+" seconds ago.");
        return (timePassed < timeoutSeconds);
    }
}
