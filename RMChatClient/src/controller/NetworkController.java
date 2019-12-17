package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.LoginData;
import model.NewUser;
import view.ViewEventHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class NetworkController {
    private final Logger logger;
    private final Properties properties;
    private ViewEventHandler viewEventHandler;

    public NetworkController(Logger logger, Properties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    //TODO This works, but is old

    public void setViewEventHandler(ViewEventHandler viewEventHandler) {
        this.viewEventHandler = viewEventHandler;
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
            String result = tcpReceive.readString();

            logger.info("Recieved message: "+result);

            //TODO add logic here
            if(result.equals("OK")){
                viewEventHandler.showInfo("Account successfully created");
            }
            else{
                viewEventHandler.showError("Account not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
            String result = tcpReceive.readString();

            logger.info("Recieved message: "+result);

            //TODO add logic here
            if(result.equals("OK")){
                viewEventHandler.showInfo("Account successfully created");
            }
            else{
                viewEventHandler.showError("Account not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Socket createSocket() throws IOException {
        //TODO Change to server
        InetAddress ip = InetAddress.getByName("localhost");
        int serverPort = properties.getInt("server.port");
        Socket socket = new Socket(ip, serverPort);
        logger.info("A new client is connected : " + socket);
        return socket;
    }
}
