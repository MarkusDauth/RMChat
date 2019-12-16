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
    private final static NetworkController instance = null;
    private static FileHandler logFileHandler;

    //TODO Experimentell; damit ViewEventHandler anfragen an den Controller weiterleiten kann
    public static NetworkController getInstance() {
        if(instance == null){
            Logger logger = Logger.getLogger("logger");

            try {
                logFileHandler = new FileHandler("logs.log",true);
                logger.addHandler(logFileHandler);
                SimpleFormatter formatter = new SimpleFormatter();
                logFileHandler.setFormatter(formatter);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return new NetworkController(logger,new Properties());
        }
        return instance;
    }

    private NetworkController(Logger logger, Properties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    //TODO This works, but is old
    public void registerNewUser(NewUser newUser) {
        logger.info("Creating new user: " + newUser.getUsername());

        try {
            //TODO Change to server
            InetAddress ip = InetAddress.getByName("localhost");
            int serverPort = properties.getInt("server.port");
            Socket socket = new Socket(ip, serverPort);

            TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());
            TcpSend tcpSend = new TcpSend(socket.getOutputStream());

            tcpSend.sendRegisterQuery(newUser);
            String result = tcpReceive.receiveString();
            logger.info("RECEIVED MESSAGE: "+result);
            if(result.equals("OK")){
                ViewEventHandler.showInfo("Account successfully created");
            }
            else{
                ViewEventHandler.showError("Account not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loginUser(LoginData loginData) {
        logger.info("Logging in: " + loginData.getUsername());

        try {
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
                ViewEventHandler.showInfo("Account successfully created");
            }
            else{
                ViewEventHandler.showError("Account not created");
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
