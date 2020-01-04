package controller.network.tcp;

import properties.Properties;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Same code for client and server
 */
public class TcpSend {
    private OutputStream out;
    private byte[] buffer = new byte[Properties.getInt("tcp.byteBufferLength")];
    int elementsInBuffer = 0;
    boolean wroteBufferToOutputStream = false;


    public TcpSend(OutputStream out) {
        this.out = out;
    }

    public void add(String string) {
        assert(!wroteBufferToOutputStream);
        for (int i = 0; i < string.length(); i++) {
            addToBuffer(string.charAt(i));
        }
        addToBuffer('\0');
    }

    private void addToBuffer(char e){
        buffer[elementsInBuffer] = (byte) e;
        elementsInBuffer++;
    }

    public void send() throws IOException {
        assert(!wroteBufferToOutputStream);
        out.write(buffer);
        wroteBufferToOutputStream = true;
    }

    public void sendError(String errorType) throws IOException {
        add("ERROR");
        add(errorType);
        send();
    }
}

