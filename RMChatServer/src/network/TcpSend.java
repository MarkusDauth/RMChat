package network;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Same code for client and server
 */
public class TcpSend {
    private OutputStream out;
    private List<Byte> bufferList = new ArrayList<>();


    public TcpSend(OutputStream out) {
        this.out = out;
    }

    public void add(String string) {
        for (int i = 0; i < string.length(); i++) {
            bufferList.add((byte) string.charAt(i));
        }
        bufferList.add((byte) '\0');
    }

    public void send() throws IOException {
        byte[] byteArray = listToPrimitiveByteArray(bufferList);
        out.write(byteArray);
    }

    private byte[] listToPrimitiveByteArray(List<Byte> bufferList) {
        byte[] byteArray = new byte[bufferList.size()];
        for(int i = 0; i < bufferList.size(); i++){
            byteArray[i] = bufferList.get(i);
        }
        return byteArray;
    }

    public void sendError(String errorType) throws IOException {
        add("ERROR");
        add(errorType);
        send();
    }
}