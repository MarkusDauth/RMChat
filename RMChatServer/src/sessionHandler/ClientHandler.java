package sessionHandler;

import sessionHandler.tcp.TcpReceive;
import sessionHandler.tcp.TcpSend;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

class ClientHandler implements Runnable {
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
                logger.info("New Client message. Code: " + code);
                switch (code) {
                    case "LOGIN":
                        SessionHandler.login(tcpSend, tcpReceive);
                        break;
                    case "REGISTER":
                        Registration.registerUser(tcpSend, tcpReceive);
                        break;
                }
                socket.close(); //Can't be in finally block, because of exception
                logger.info("Socket closed. Port: " + socket);
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }
}
