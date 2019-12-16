package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.NewUser;
import view.ViewEventHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
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
            Logger logger;
            logger = Logger.getLogger("logger");

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

    public void registerNewUser(NewUser newUser) {
        logger.info("Creating new user: " + newUser.getUserName());

        try {
            //TODO Change to server
            InetAddress ip = InetAddress.getByName("localhost");
            int serverPort = properties.getInt("server.port");
            Socket socket = new Socket(ip, serverPort);

            //TODO Change to ByteBuffer
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

            /**

            //Done
            logger.info("Closing this connection : " + socket);
            socket.close();
            logger.info("Connection closed");

            // closing resources
            inputStream.close();
            outputStream.close();

             **/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
