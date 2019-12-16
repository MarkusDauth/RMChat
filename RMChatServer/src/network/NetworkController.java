package network;

import network.tcp.TcpReceive;
import network.tcp.TcpSend;
import properties.Properties;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public class NetworkController {
    private Properties properties;
    private Logger logger;

    public NetworkController(Logger logger, Properties properties) {
        this.properties = properties;
        this.logger = logger;
    }

    //TODO Rename test method
    public void tcpTest() throws IOException {
        int serverPort = properties.getInt("server.port");
        ServerSocket serverSocket = new ServerSocket(serverPort);

        while (true) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();
                logger.info("A new client is connected : " + socket);

                TcpReceive tcpReceive = new TcpReceive(socket.getInputStream());
                TcpSend tcpSend = new TcpSend(socket.getOutputStream());

                //TODO TESTING
                {
                    System.out.println(tcpReceive.receiveString());
                    tcpSend.sendString("OK");
                }

                //New Thread
                //Runnable runnable  = new ClientHandler(socket, inputStream, outputStream, logger);
                //Thread thread = new Thread(runnable);
                //thread.start();

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }
}
