import java.sql.*;


public class BaseAuthService implements AuthService {
    private Connection connect;

    @Override
    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connect = DriverManager.getConnection("jdbc:sqlite:main.db");
    }

    @Override
    public void disconnect() {
        try{
            connect.close();
        }catch(SQLException e)  {
            e.printStackTrace();
        }
    }

    public Boolean authentication(String login, String password)   {
        try{
            PreparedStatement ps = connect.prepareStatement(
                    "SELECT id FROM users WHERE login=? AND password=?;");
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if(rs.next())   {
                return true;
            }
        }catch(SQLException e)  {
            e.printStackTrace();
        }
        return false;
    }
}
