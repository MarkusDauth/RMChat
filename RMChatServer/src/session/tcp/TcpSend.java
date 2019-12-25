package session.tcp;

import properties.Properties;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Same code for client and server
 */
public class TcpSend {
    private static final Logger logger = Logger.getLogger("logger");


    private final OutputStream out;
    private final ByteBuffer bbuf;


    public TcpSend(OutputStream out) {
        this.out = out;
        int bufferLength = Properties.getInt("tcp.byteBufferLength");
        byte[] buffer = new byte[bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    public void add(String string) {
        for (int i = 0; i < string.length(); i++) {
            bbuf.put((byte) string.charAt(i));
        }
        bbuf.put((byte) '\0');

        //logger.info("SENT: "+string);
    }

    public void send() throws IOException {
        out.write(bbuf.array());
    }

    public void sendError(String errorType) throws IOException {
        add("ERROR");
        add(errorType);
        send();

    }
}

