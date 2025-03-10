package network;

import network.TcpReceive;
import network.TcpSend;
import session.Registration;
import session.SessionHandler;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private static final Logger logger = Logger.getLogger("logger");
    private final TcpSend tcpSend;
    private final TcpReceive tcpReceive;

    public ClientHandler(Socket socket, OutputStream outputStream, InputStream inputStream) {
        this.socket = socket;
        tcpSend = new TcpSend(outputStream);
        tcpReceive = new TcpReceive(inputStream);
    }

    @Override
    public void run() {
        {
            try {
                tcpReceive.receive();
                String code = tcpReceive.readNextString();
                logger.fine("New Client message. Code: " + code);
                switch (code) {
                    case "ALIVE":
                        SessionHandler.updateSessionAlive(tcpSend, tcpReceive);
                        break;
                    case "SENDMSG":
                        SessionHandler.sendMessage(tcpSend, tcpReceive);
                        break;
                    case "LOGIN":
                        SessionHandler.login(socket, tcpSend, tcpReceive);
                        break;
                    case "ADDFRIEND":
                        SessionHandler.addFriend(tcpSend, tcpReceive);
                        break;
                    case "REGISTER":
                        Registration.registerUser(tcpSend, tcpReceive);
                        break;
                }
                socket.close(); //Can't be in finally block, because of exception
                logger.fine("Socket closed. Port: " + socket);
            } catch (IOException e) {
                logger.info(String.valueOf(e.getMessage()));
            }
        }
    }
}
