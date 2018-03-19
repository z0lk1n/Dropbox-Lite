package auth_reg;

import java.sql.*;


public class ImlAuthService implements AuthService {
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

    public int getIdByLoginAndPass(String login, String pass)   {
        try{
            ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE login = '" + login + "' AND password = '" + pass + "';");
            if(rs.next())   {
                return rs.getInt("id");
            }
        }catch(SQLException e)  {
            e.printStackTrace();
        }
        return -1;
    }
}
