public interface ConnectInterface {
    void setAuthorized(boolean authorized, String client);
    void connect();
    void login(String login, String password);
    void registration(String login, String password);
}
