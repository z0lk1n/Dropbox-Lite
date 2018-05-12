public class AuthMessage extends Message {
    private String password;
    private String client;

    AuthMessage(Commands command) {
        super(command);
    }

    AuthMessage(Commands command, String client) {
        super(command);
        this.client = client;
    }

    AuthMessage(Commands command, String client, String password) {
        super(command);
        this.client = client;
        this.password = password;
    }

    @Override
    public Commands getCommand() {
        return super.getCommand();
    }

    String getClient() {
        return client;
    }

    String getPassword() {
        return password;
    }
}

