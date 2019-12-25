package controller;

import controller.tcp.TcpReceive;
import controller.tcp.TcpSend;
import model.NewUser;
import view.Views;

import java.net.Socket;
import java.util.logging.Logger;

public class RegistrationTask implements Runnable {

    private Logger logger = Logger.getLogger("logger");
    private Views views;
    private NewUser newUser;

    RegistrationTask(NewUser newUser, Views views) {
        this.newUser = newUser;
        this.views = views;
    }

    @Override
    public void run() {
        logger.info("Creating new user: " + newUser.getUsername());

        try {

            Socket socket = NetworkController.createSocket();
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
            logger.info("Received message: "+result);
            views.showMessage(tcpReceive.readNextString());

        } catch (Exception e) {
            logger.severe(e.getMessage());
            views.showMessage("Unexpected");
        }
    }
}
