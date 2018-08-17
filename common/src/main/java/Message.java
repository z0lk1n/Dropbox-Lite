import java.io.Serializable;

public abstract class Message implements Serializable {
    private Commands command;

    Message(Commands command) {
        this.command = command;
    }

    protected Commands getCommand() {
        return command;
    }
}
