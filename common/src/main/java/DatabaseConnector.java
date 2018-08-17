import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DatabaseConnector {
    private static volatile DatabaseConnector instance;
    private Connection connect;

    private DatabaseConnector() {
    }

    static DatabaseConnector getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnector.class) {
                if (instance == null) {
                    instance = new DatabaseConnector();
                }
            }
        }
        return instance;
    }

    void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("jdbc:sqlite:main.db");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void disconnect() {
        try {
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    Connection getConnect() {
        return connect;
    }
}
