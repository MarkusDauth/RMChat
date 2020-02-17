package controller;

import controller.network.*;
import model.*;
import properties.Properties;
import view.Views;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * In dieser Klasse werden die Anfragen des GUI-Thread auf neuen Threads bearbeitet.
 */
public class Controller {

    private static final Logger logger = Logger.getLogger("logger");

    private static String sessionID = "notSet";
    private static String username;
    private static String password;
    private static UserStatus userStatus = UserStatus.Offline;
    private Views views;
    private ServerSocket serverSocket;

    public Controller(){
        serverSocket = createServerSocket();
    }

    public static void setSessionID(String sessionID) { Controller.sessionID = sessionID; }

    public static void setUsername(String username) { Controller.username = username;}

    public static void setUserStatus(UserStatus userStatus) { Controller.userStatus = userStatus; }

    public static void setPassword(String password) { Controller.password = password; }

    public static String getUsername() {
        return username;
    }

    public static String getSessionID() { return sessionID; }

    public static UserStatus getUserStatus() { return userStatus; }

    public static String getPassword() { return password; }

    public static boolean isSessionIDSet(){
        return !sessionID.equals("notSet");
    }

    public void setViews(Views views) {
        this.views = views;
    }

    public void registerNewUser(NewUser newUser) {
        new Thread(new RegistrationTask(newUser,views)).start();
    }

    public void loginUser(LoginData loginData) {
        username = loginData.getUsername();
        new Thread(new LoginTask(loginData, serverSocket ,views)).start();
    }

    public void sendMessage(Message message) {
        new Thread(new SendMessageTask(message,views)).start();
    }

    public void addFriend(Friend friend) {
        new Thread(new AddFriendTask(friend,views)).start();
    }

    /**
     * Erstellt einen Socket mit einem timeout von 20 Sekunden
     * @return socket
     * @throws IOException
     */
    public static Socket createSocket() throws IOException {
        String serverIP = Properties.getString("server.ip");
        InetAddress ip = InetAddress.getByName(serverIP);
        int serverPort = Properties.getInt("server.port");
        Socket socket = new Socket(ip, serverPort);
        int timeout = Properties.getInt("tcp.generalTimeout.seconds")*1000;
        socket.setSoTimeout(timeout);
        logger.info("Connected to Server: " + socket);
        return socket;
    }


    private ServerSocket createServerSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocket;
    }
}
