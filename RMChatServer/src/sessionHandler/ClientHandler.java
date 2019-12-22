package sessionHandler;

import database.DatabaseInterface;
import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private Socket socket;
    private static Logger logger = Logger.getLogger("logger");
    private TcpSend tcpSend;
    private TcpReceive tcpReceive;

    public ClientHandler(Socket socket,
                         OutputStream outputStream,
                         InputStream inputStream
    ) {
        this.socket = socket;
        tcpSend = new TcpSend(outputStream);
        tcpReceive = new TcpReceive( inputStream);
    }

    @Override
    public void run() {
        {
            try {
                tcpReceive.receive();
                String code = tcpReceive.readString();
                logger.info("New Client message. Code: " + code);
                switch (code) {
                    case "LOGIN":
                        handleLogin();
                        break;
                    case "REGISTER":
                        Registration.registerUser(tcpSend, tcpReceive);
                        break;
                }
                socket.close(); //Can't be in finally block, because of exception
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }
    }

    //TODO implement
    private void handleLogin() throws IOException {
        tcpSend.add("OK");
        tcpSend.send();
    }
}
