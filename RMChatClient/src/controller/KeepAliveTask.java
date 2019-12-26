package controller;

import properties.Properties;
import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.Friend;
import model.LoginData;
import view.Views;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class KeepAliveTask implements Runnable{

    private static Logger logger = Logger.getLogger("logger");

    private Views views;
    private int keepAliveTimeout = Properties.getInt("client.keepAliveTimeout.seconds");
    public KeepAliveTask(Views views) {
        this.views = views;
    }

    @Override
    public void run() {

        while(true){
            if(NetworkController.getUserStatus().equals(UserStatus.Online))
                keepAliveRoutine();
            else {
                try {
                    reconnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                TimeUnit.SECONDS.sleep(keepAliveTimeout);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private void keepAliveRoutine() {
        logger.fine("Sending Keep-Alive-Message");

        try{
            Socket socket = NetworkController.createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("ALIVE");
            tcpSend.add(NetworkController.getSessionID());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String code = tcpReceive.readNextString();
            logger.fine("Received message: "+code);
            processCode(code,tcpReceive);


        } catch(IOException e) {
            views.showMessage("Unexpected");
            views.setIndexStatus("OfflineStatus");
            NetworkController.setUserStatus(UserStatus.Offline);
            logger.severe(e.getMessage());
        }
    }

    private void processCode(String code, TcpReceive tcpReceive) {
        if(code.equals("OKALV")){
            views.setIndexStatus("OnlineStatus");
            List<Friend> friends = parseFriends(tcpReceive);
            views.refreshFriendList(friends);
            return;
        }
        //TODO better error-handling
        if(tcpReceive.readNextString().equals("UserNotLoggedIn")){
            views.setIndexStatus("OfflineStatus");
            views.showMessage("UserNotLoggedIn");
            NetworkController.setUserStatus(UserStatus.Offline);
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

    public void reconnect() throws InterruptedException {
        LoginData loginData = new LoginData(NetworkController.getUsername(),NetworkController.getPassword());
        int retries = 0;
        while( isUserOffline() && retries < Properties.getInt("client.maxReconnect")) {
            retries++;
            logger.info("Trying to reconnect to the server. Attempt: "+retries);
            new LoginTask(loginData, views).run();
            TimeUnit.SECONDS.sleep(keepAliveTimeout);
        }
        evaluateReconnectAttempt();
    }

    private void evaluateReconnectAttempt() {
        if(isUserOffline()){
            logger.info("Reconnect was unsuccessful. Terminating Application..");
            System.exit(1);
        }
        else{
            logger.info("Reconnect successful. SessionID: "+NetworkController.getSessionID());
        }
    }

    private boolean isUserOffline(){
        return NetworkController.getUserStatus().equals(UserStatus.Offline);
    }

}
