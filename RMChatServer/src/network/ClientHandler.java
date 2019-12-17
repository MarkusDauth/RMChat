package network;

import network.tcp.TcpReceive;
import network.tcp.TcpSend;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    final Socket socket;
    final Logger logger;
    final OutputStream outputStream;
    final InputStream inputStream;
    TcpSend tcpSend;
    TcpReceive tcpReceive;

    public ClientHandler(Socket socket, OutputStream outputStream,  InputStream inputStream, Logger logger) {
        this.socket = socket;
        this.logger = logger;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        {
            try {
                tcpSend = new TcpSend(outputStream);
                tcpReceive = new TcpReceive( inputStream);

                tcpReceive.receive();
                logger.info("Message recieved");
                String code = tcpReceive.readString();
                logger.info("Client sent code: " + code);
                switch (code) {
                    case "LOGIN":
                        handleLogin();
                        break;
                    case "REGISTER":
                        handleRegister();
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleLogin() throws IOException {
        //TODO login logic here
        tcpSend.add("OK");
        tcpSend.send();
    }

    private void handleRegister() throws IOException {
        //TODO Register logic here
        tcpSend.add("OK");
        tcpSend.send();
    }
}
