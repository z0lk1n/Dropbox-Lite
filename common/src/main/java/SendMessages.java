import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

class SendMessages {
    private OutputStream outputStream;

    SendMessages(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    synchronized void send(Object msg) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
