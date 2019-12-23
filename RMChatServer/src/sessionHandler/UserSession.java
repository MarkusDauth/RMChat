package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class UserSession {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    private Socket socket;
    private TcpSend tcpSend;
    private TcpReceive tcpReceive;

    private String username;
    private String password;

    private String sessionId;

    public String getUsername() {
        return username;
    }

    public UserSession(Socket socket, TcpSend tcpSend, TcpReceive tcpReceive) {
        this.socket = socket;
        this.tcpSend = tcpSend;
        this.tcpReceive = tcpReceive;
        this.username = username;
        this.password = password;
    }

    public boolean validateCredentials() throws IOException {
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


    public boolean setupSession() throws IOException {
        username = tcpReceive.readNextString();
        password = tcpReceive.readNextString();
        logger.info("Trying to login: " + username);

        if (!validateCredentials()) {
            return false;
        }
        createSessionId();
        return true;
    }

    private void createSessionId(){
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(username);
        sb.append(rand.nextInt(1000));
        sessionId = sb.toString();
    }

    public void finishLogin() throws IOException {
        tcpSend.add("OKLOG");
        tcpSend.add(sessionId);
        tcpSend.send();
        logger.info("Successfull Login: " + username);
    }
}
