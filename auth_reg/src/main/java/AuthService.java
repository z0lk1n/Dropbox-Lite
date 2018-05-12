public interface AuthService {
    void connect();

    void disconnect();

    Boolean authentication(String login, String password);
}
