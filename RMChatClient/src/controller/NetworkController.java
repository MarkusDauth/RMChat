package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.LoginData;
import model.NewUser;
import view.LoginEventHandler;
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
        logger.info("Creating new user: " + newUser.getUsername());

        try {

            Socket socket = createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("REGISTER");
            tcpSend.add(newUser.getUsername());
            tcpSend.add(newUser.getPassword());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String result = tcpReceive.readNextString();

            logger.info("Recieved message: "+result);

            //TODO add logic here
            if(result.equals("OK")){
                Views.showInfo("Account successfully created");
            }
            else{
                Views.showError("Account not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO implement
    public void loginUser(LoginData loginData) {
        logger.info("Logging in: " + loginData.getUsername());

        try{
            Socket socket = createSocket();
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());
            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());

            //Data to send here
            tcpSend.add("LOGIN");
            tcpSend.add(loginData.getUsername());
            tcpSend.add(loginData.getPassword());
            tcpSend.send();

            //Server response here
            tcpReceive.receive();
            String result = tcpReceive.readNextString();

            logger.info("Recieved message: "+result);

            //TODO add logic here
            if(result.equals("OK")){
                Views.showInfo("Account successfully created");
            }
            else{
                Views.showError("Account not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Socket createSocket() throws IOException {
        //TODO Change to server
        InetAddress ip = InetAddress.getByName("localhost");
        int serverPort = Properties.getInt("server.port");
        Socket socket = new Socket(ip, serverPort);
        logger.info("A new client is connected : " + socket);
        return socket;
    }
}
