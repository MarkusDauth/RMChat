package network.tcp;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class TcpReceive {
    InputStream in;
    //TODO not sure if needed
    int offset, readBytes, currentLength, length;
    byte[] buffer;
    ByteBuffer bbuf;
    int bufferLength = 256;

    public TcpReceive(InputStream in) {
        this.in = in;
        buffer = new byte[bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    /**
     * Loads entire TCP message into bbuf.
     * Read the content with the read methods
     *
     * @throws Exception
     */
    public void receive() throws Exception {
        int readbytes = in.read(buffer);
        for (int i = 0; i < readbytes; i++) {
            bbuf.put(buffer[i]);
        }
    }

    /**
     * Read next String from bbuf
     *
     * @return
     */
    public String readString() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        while ((char) buffer[i] != '\0') {
            stringBuilder.append((char) buffer[i]);
            i++;
        }

        return stringBuilder.toString();
    }


    //TODO remove old
    public String receiveString() throws Exception {

        int readbytes = in.read(buffer);
        StringBuilder stringBuilder = new StringBuilder();

        //TODO instead of checking if byte equals \0 use readbytes
        for (int i = 0; i < bufferLength; i++) {
            if ((char) buffer[i] != '\0')
                stringBuilder.append((char) buffer[i]);
            else {
                break;
            }
        }
        return stringBuilder.toString();
    }
}
