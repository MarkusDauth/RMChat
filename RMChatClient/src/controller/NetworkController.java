package controller;

import model.LoginData;
import model.NewUser;
import view.Views;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class NetworkController {
    private static Logger logger = Logger.getLogger("logger");
    private Views views;

    public void setViews(Views views) {
        this.views = views;
    }

    public void registerNewUser(NewUser newUser) {
        new Thread(new RegistrationTask(newUser,views,logger)).start();
    }

    //TODO implement
    public void loginUser(LoginData loginData) {
        new Thread(new LoginTask(loginData,views,logger)).start();
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
