public interface Const {
    int SERVER_PORT = 8981;
    String SERVER_ADDRESS = "localhost";

    String TITLE_FORM = "Dropbox-Lite";

    String CORE_PATH = "/";
    String SYSTEM_SYMBOL = "/";
    String CLOSE_CONNECTION = "/end";
    String AUTH = "/auth ";
    String AUTH_SUCCESSFUl = "/authok ";
    String FILES_LIST = "/fileslist";
    String DELETE_FILE = "/del ";
    String DOWNLOAD_FILE = "/get ";
    String UPLOAD_FILE = "/add ";
    String CHANGE_FILE = "/edit ";

    String ACC_BUSY = "Account is busy!";
    String FAIL_AUTH_SERVICE = "Failed to start the authorization service";
    String CLIENT_CONNECT = "Client connected: ";
    String RUN_SERVER = "Server running... Waiting clients...";
    String LOST_SERVER = "The server stopped responding, try connecting again";
    String FAIL_CONNECT_SERVER = "Could not connect to the server. Check the network connection";
    String INCOMPLETE_AUTH = "Incomplete authorization data specified";
    String OOPS = "Oops, we are having problems";
}
