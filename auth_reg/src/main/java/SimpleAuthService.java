import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleAuthService implements AuthService {
    private Connection connect;

    SimpleAuthService(Connection connect) {
        this.connect = connect;
    }

    @Override
    public Boolean authentication(String login, String password) {
        try {
            PreparedStatement ps = connect.prepareStatement("SELECT id FROM users WHERE login=? AND password=?;");
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
