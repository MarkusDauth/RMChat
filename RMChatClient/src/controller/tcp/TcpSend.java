package controller.tcp;

import model.NewUser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class TcpSend {
    OutputStream out = null;
    int bufferLength = 256;
    byte [] buffer;
    ByteBuffer bbuf;
    int writeBytes;

    public TcpSend(OutputStream out) {
        this.out = out;
        buffer = new byte [bufferLength];
        bbuf = ByteBuffer.wrap(buffer);
    }

    public void add(String string){
        for(int i = 0; i < string.length();i++){
            bbuf.put((byte) string.charAt(i));
        }
        bbuf.put((byte)'\0');
    }

    public void send() throws IOException {
        out.write(bbuf.array());
    }

    //TODO remove old
    public void sendRegisterQuery(NewUser newUser) throws Exception {

        //TODO Exception handling (buffer too small,...)
        String query = "REGERR";
        for(int i = 0; i < query.length();i++){
            bbuf.put((byte) query.charAt(i));
        }
        //TODO / is used for testing
        bbuf.put((byte)'/');
        for(int i = 0; i < newUser.getUsername().length(); i++){
            bbuf.put((byte) newUser.getUsername().charAt(i));
        }
        bbuf.put((byte)'/');
        for(int i = 0; i < newUser.getPassword().length(); i++) {
           bbuf.put((byte) newUser.getPassword().charAt(i));
        }
        bbuf.put((byte)'\0');
        out.write(bbuf.array());
    }
}
