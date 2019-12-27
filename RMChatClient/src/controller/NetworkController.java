package controller;

import model.*;
import properties.Properties;
import view.Views;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class NetworkController {

    private static Logger logger = Logger.getLogger("logger");

    private static String sessionID = "notSet";
    private static String username;
    private static String password;
    private static UserStatus userStatus = UserStatus.Offline;
    private Views views;

    public static void setSessionID(String sessionID) { NetworkController.sessionID = sessionID; }
    public static void setUsername(String username) { NetworkController.username = username;}
    public static void setUserStatus(UserStatus userStatus) { NetworkController.userStatus = userStatus; }
    public static void setPassword(String password) { NetworkController.password = password; }
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
        new Thread(new LoginTask(loginData,views)).start();
    }

    public void sendMessage(Message message) {
        new Thread(new SendMessageTask(message,views)).start();
    }

    public void addFriend(Friend friend) {
        new Thread(new AddFriendTask(friend,views)).start();
    }

    public void sendFriendRequestAnswer(FriendRequest friendRequest) {
        new Thread(new FriendRequestAnswerTask(friendRequest,views)).start();
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
