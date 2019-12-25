package controller;

import model.LoginData;
import model.Message;
import model.NewUser;
import view.Views;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;

public class NetworkController {
    public static Optional<String> sessionID = Optional.empty();
    private static String username;

    private static Logger logger = Logger.getLogger("logger");
    private Views views;

    public static String getUsername() {
        return username;
    }

    public void setViews(Views views) {
        this.views = views;
    }

    public void registerNewUser(NewUser newUser) {
        new Thread(new RegistrationTask(newUser,views,logger)).start();
    }

    public void loginUser(LoginData loginData) {
        username = loginData.getUsername();
        new Thread(new LoginTask(loginData,views,logger)).start();
    }

    public void sendMessage(Message message) {
        new Thread(new SendMessageTask(message,views,logger)).start();
    }

    static Socket createSocket() throws IOException {
        String serverIP = Properties.getString("server.ip");
        InetAddress ip = InetAddress.getByName(serverIP);
        int serverPort = Properties.getInt("server.port");
        Socket socket = new Socket(ip, serverPort);
        logger.info("Connected to Server: " + socket);
        return socket;
    }
}
