import java.sql.*;


public class BaseAuthService implements AuthService {
    private Connection connect;
    private Statement stmt;

    @Override
    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connect = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connect.createStatement();
    }

    @Override
    public void disconnect() {
        try{
            stmt.close();
            connect.close();
        }catch(SQLException e)  {
            e.printStackTrace();
        }
    }

    public Boolean authentication(String login, String pass)   {
        try{
            ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE login = '" + login + "' AND password = '" + pass + "';");
            if(rs.next())   {
                return true;
            }
        }catch(SQLException e)  {
            e.printStackTrace();
        }
        return false;
    }
}
