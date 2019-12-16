package network.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class TcpSend {
    private OutputStream out;
    private final int bufferLength = 256;
    private byte [] buffer;
    private ByteBuffer bbuf;


    public TcpSend (OutputStream out) {
        this.out = out;
        buffer = new byte [bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    //TODO sendInt()

    public void sendString(String s) throws Exception {
        if(s.length() >= bufferLength){
            throw new Exception("String too long");
        }
        for(int i = 0; i < s.length();i++){
            bbuf.put((byte) s.charAt(i));
        }
        bbuf.put((byte)'\0');
        out.write(bbuf.array());
    }
}
