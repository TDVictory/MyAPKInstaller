package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyMessage {
    private Header header;

    private byte[] body;

    public MyMessage(Header header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public byte[] bodyToByte(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

}
