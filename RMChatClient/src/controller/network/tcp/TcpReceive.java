package controller.network.tcp;

import properties.Properties;

import java.io.IOException;
import java.io.InputStream;

/**
 * Same Code for server and client
 */
public class TcpReceive {
    private InputStream in;
    private byte[] buffer = new byte[Properties.getInt("tcp.byteBufferLength")];
    private int readPosition = 0;
    private int readBytes;

    public TcpReceive(InputStream in) {
        this.in = in;
    }

    /**
     * Loads entire TCP message into ByteBuffer.
     * Read the content with the read methods
     *
     * @throws IOException
     */
    public void receive() throws IOException {
        readBytes = in.read(buffer);
    }
    /**
     * Read next String from ByteBuffer
     *
     * @return
     */
    public String readNextString() {
        StringBuilder stringBuilder = new StringBuilder();
        while ((readPosition < readBytes) && (char) buffer[readPosition] != '\0') {
            stringBuilder.append((char) buffer[readPosition]);
            readPosition++;
        }
        readPosition++;

        return stringBuilder.toString();
    }
}