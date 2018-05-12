public class AuthMessage extends Message {
    private String password;
    private String client;

    public AuthMessage(Commands command) {
        super(command);
    }

    public AuthMessage(Commands command, String client) {
        super(command);
        this.client = client;
    }

    public AuthMessage(Commands command, String client, String password) {
        super(command);
        this.client = client;
        this.password = password;
    }

    @Override
    public Commands getCommand() {
        return super.getCommand();
    }

    public String getClient() {
        return client;
    }

    public String getPassword() {
        return password;
    }
}

