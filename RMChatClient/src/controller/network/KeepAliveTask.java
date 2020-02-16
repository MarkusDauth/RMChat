package controller.network;

import controller.Controller;
import controller.UserStatus;
import properties.Properties;
import controller.network.tcp.TcpReceive;
import controller.network.tcp.TcpSend;
import model.Friend;
import model.LoginData;
import view.Views;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class KeepAliveTask implements Runnable{

    private static final Logger logger = Logger.getLogger("logger");

    private final Views views;
    private final int keepAliveTimeout = Properties.getInt("client.keepAliveTimeout.seconds");
    private ServerSocket serverSocket;

    public KeepAliveTask(ServerSocket serverSocket, Views views) {
        this.views = views;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {

        while(true){
            try {
                keepAliveRoutine();
                TimeUnit.SECONDS.sleep(keepAliveTimeout);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private void keepAliveRoutine() throws InterruptedException {
        logger.fine("Sending Keep-Alive-Message");

        try{
            Socket socket = Controller.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("ALIVE");
            tcpSend.add(Controller.getSessionID());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();
            logger.fine("Received message: "+code);
            processCode(code,tcpReceive);


        } catch(IOException e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
            views.setChatStatus("OfflineStatus");
            Controller.setUserStatus(UserStatus.Offline);
            reconnectRoutine();
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) throws InterruptedException {
        if(code.equals("OKALV")){
            views.setChatStatus("OnlineStatus");
            List<Friend> friends = parseFriends(tcpReceive);
            views.refreshFriendList(friends);
            return;
        }
        if(tcpReceive.readNextString().equals("UserNotLoggedIn")){
            views.setChatStatus("OfflineStatus");
            Controller.setUserStatus(UserStatus.Offline);
            reconnectRoutine();
        }
    }

    private List<Friend> parseFriends(TcpReceive tcpReceive) {
        logger.info("Parsing Friends");
        List<Friend> friendList = new ArrayList<>();

        while(true){
            String friendName = tcpReceive.readNextString();
            UserStatus status = "1".equals(tcpReceive.readNextString())
                    ? UserStatus.Online : UserStatus.Offline;
            if(friendName.equals(""))
                break;
            Friend friend = new Friend(friendName,status);
            friendList.add(friend);
        }
        return friendList;
    }

    private void reconnectRoutine() throws InterruptedException {
        LoginData loginData = new LoginData(Controller.getUsername(), Controller.getPassword());
        int retries = 0;
        while( isUserOffline() && retries < Properties.getInt("client.maxReconnect")) {
            retries++;
            logger.info("Trying to reconnect to the server. Attempt: "+retries);
            views.setReconnectText(retries,!isUserOffline());
            new LoginTask(loginData, serverSocket, views).run();
            TimeUnit.SECONDS.sleep(keepAliveTimeout);
        }
        evaluateReconnectAttempt();
    }

    private void evaluateReconnectAttempt() throws InterruptedException {
        if(isUserOffline()){
            String errorMessage = Properties.getString("ReconnectError");
            logger.severe(errorMessage);
            views.showMessage("ReconnectError");
            TimeUnit.SECONDS.sleep(5);
            System.exit(1);
        }
        else{
            logger.info("Reconnect successful. SessionID: "+ Controller.getSessionID());
            views.setReconnectText(0,!isUserOffline());
            views.showMessage("OKReconnect");
        }
    }

    private boolean isUserOffline(){
        return Controller.getUserStatus().equals(UserStatus.Offline);
    }

}
