package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class UserSession {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    private TcpSend tcpSend;
    private TcpReceive tcpReceive;

    private String username;
    private String password;

    private long sessionId;

    public long getSessionId() {
        return sessionId;
    }

    public UserSession(TcpSend tcpSend, TcpReceive tcpReceive, String username, String password) {
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
        if (!validateCredentials()) {
            return false;
        }
        createSessionId();
        return true;

    }

    private void createSessionId(){
        SecureRandom rand = new SecureRandom();
        sessionId = rand.nextInt(1000);
    }
}
