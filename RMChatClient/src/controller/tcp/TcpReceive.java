package controller.tcp;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class TcpReceive {
    InputStream in;
    //TODO not sure if needed
    int offset, readBytes, currentLength, length;
    byte [] buffer;
    ByteBuffer bbuf;
    int bufferLength = 256;

    public TcpReceive (InputStream in) {
        this.in = in;
        buffer = new byte [bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    //TODO receiveInt()

    public String receiveString() throws Exception {

        int readbytes = in.read(buffer);
        StringBuilder stringBuilder = new StringBuilder();

        //TODO instead of checking if byte equals \0 use readbytes
        for(int i = 0; i < bufferLength;i++){
            if((char) buffer[i]!='\0')
                stringBuilder.append((char)buffer[i]);
            else{
                break;
            }
        }
        return stringBuilder.toString();
    }
}
