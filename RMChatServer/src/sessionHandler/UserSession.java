package sessionHandler;

import database.DatabaseInterface;
import database.TextfileDatabase;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.IOException;
import java.util.logging.Logger;

public class UserSession {
    private static final DatabaseInterface database = TextfileDatabase.getInstance();
    private static final Logger logger = Logger.getLogger("logger");

    private TcpSend tcpSend;
    private TcpReceive tcpReceive;

    private String username;
    private String password;

    public TcpSend getTcpSend() {
        return tcpSend;
    }

    public void setTcpSend(TcpSend tcpSend) {
        this.tcpSend = tcpSend;
    }

    public TcpReceive getTcpReceive() {
        return tcpReceive;
    }

    public void setTcpReceive(TcpReceive tcpReceive) {
        this.tcpReceive = tcpReceive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
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
}
