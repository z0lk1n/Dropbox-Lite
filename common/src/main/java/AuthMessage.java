public class AuthMessage extends Message {
    private String password;

    public AuthMessage(Commands command, String client) {
        super(command, client);
    }

    public AuthMessage(Commands command, String client, String password) {
        super(command, client);
        this.password = password;
    }

    @Override
    public Commands getCommand() {
        return super.getCommand();
    }

    @Override
    public String getClient() {
        return super.getClient();
    }

    public String getPassword() {
        return password;
    }
}

