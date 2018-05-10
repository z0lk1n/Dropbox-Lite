import java.io.Serializable;

public abstract class Message implements Serializable {
    private Commands command;
    private String client;

    public Message(Commands command, String client) {
        this.command = command;
        this.client = client;
    }

    public Commands getCommand() {
        return command;
    }

    public String getClient() {
        return client;
    }
}
