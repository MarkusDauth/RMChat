package controller.tcp;

import model.NewUser;

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

    public void sendInt(int i){

    }

    public void sendRegisterQuery(NewUser newUser) throws Exception {

        //TODO Exception handling (buffer too small,...)
        String query = "REGIST";
        for(int i = 0; i < query.length();i++){
            bbuf.put((byte) query.charAt(i));
        }
        //TODO / is used for testing
        bbuf.put((byte)'/');
        for(int i = 0; i < newUser.getUserName().length();i++){
            bbuf.put((byte) newUser.getUserName().charAt(i));
        }
        bbuf.put((byte)'/');
        for(int i = 0; i < newUser.getPassword().length(); i++) {
           bbuf.put((byte) newUser.getPassword().charAt(i));
        }
        bbuf.put((byte)'\0');
        out.write(bbuf.array());
    }
}
