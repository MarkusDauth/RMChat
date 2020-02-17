package network;

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

    /**
     * Used to create a Buffer of String which represent the message to be sent
     * Each add(...) call adds a String to the buffer, where each String is sepereated by '\0'
     * @param string the parameter to be sent
     */
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

    /**
     * Send the loaded String-buffer via TCP
     * @throws IOException
     */
    public void send() throws IOException {
        assert(!wroteBufferToOutputStream);
        out.write(buffer);
        wroteBufferToOutputStream = true;
    }

    /**
     * Wrapper-Method for add(String string) to send Error-Messages
     * Each TCP-Message starts with "ERROR" and the corresponding Error-Type
     * @param errorType
     * @throws IOException
     */
    public void sendError(String errorType) throws IOException {
        add("ERROR");
        add(errorType);
        send();
    }
}

