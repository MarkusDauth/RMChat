package controller.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Same Code for server and client
 */
public class TcpReceive {
    private InputStream in;
    private byte[] buffer;
    private ByteBuffer bbuf;
    private int bufferLength = 256;
    private int readPosition = 0;

    public TcpReceive(InputStream in) {
        this.in = in;
        buffer = new byte[bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    /**
     * Loads entire TCP message into ByteBuffer.
     * Read the content with the read methods
     *
     * @throws Exception
     */
    public void receive() throws IOException {
        int readbytes = in.read(buffer);
        for (int i = 0; i < readbytes; i++) {
            bbuf.put(buffer[i]);
        }
        readPosition = 0;
    }

    /**
     * Read next String from ByteBuffer
     *
     * @return
     */
    public String readNextString() {
        StringBuilder stringBuilder = new StringBuilder();

        while ((char) buffer[readPosition] != '\0') {
            stringBuilder.append((char) buffer[readPosition]);
            readPosition++;
        }
        readPosition++;
        return stringBuilder.toString();
    }
}
