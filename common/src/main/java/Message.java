import java.io.Serializable;

public abstract class Message implements Serializable {
    private Commands command;

    public Message(Commands command) {
        this.command = command;
    }

    public Commands getCommand() {
        return command;
    }
}
