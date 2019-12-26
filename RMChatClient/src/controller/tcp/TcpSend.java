package controller.tcp;

import properties.Properties;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Same code for client and server
 */
public class TcpSend {
    private OutputStream out;
    private final int bufferLength = Properties.getInt("tcp.byteBufferLength");
    private byte[] buffer;
    private ByteBuffer bbuf;


    public TcpSend(OutputStream out) {
        this.out = out;
        buffer = new byte[bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    public void add(String string) {
        for (int i = 0; i < string.length(); i++) {
            bbuf.put((byte) string.charAt(i));
        }
        bbuf.put((byte) '\0');
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

