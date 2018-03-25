import java.sql.SQLException;

public interface AuthService {
    void connect() throws ClassNotFoundException, SQLException;
    void disconnect();
}
