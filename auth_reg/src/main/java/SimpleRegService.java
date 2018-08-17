import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleRegService implements RegService {
    private Connection connect;

    SimpleRegService(Connection connect) {
        this.connect = connect;
    }

    @Override
    public Boolean registration(String login, String password) {
        try {
            PreparedStatement ps = connect.prepareStatement("SELECT id FROM users WHERE login=?;");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps = connect.prepareStatement("INSERT INTO users (login, password) VALUES(?, ?);");
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
