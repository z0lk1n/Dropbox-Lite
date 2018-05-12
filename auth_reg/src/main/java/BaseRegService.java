import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseRegService extends BaseAuthService implements RegService {
    @Override
    public void connect() {
        super.connect();
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }

    @Override
    public Connection getConnect() {
        return super.getConnect();
    }

    @Override
    public Boolean registration(String login, String password) {
        try {
            PreparedStatement ps = getConnect().prepareStatement("SELECT id FROM users WHERE login=?;");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                ps = getConnect().prepareStatement("INSERT INTO users (login, password) VALUES(?, ?);");
                ps.setString(1, login);
                ps.setString(2, password);
                int result = ps.executeUpdate();

                if (result > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
