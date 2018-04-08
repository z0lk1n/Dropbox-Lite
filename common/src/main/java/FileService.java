public interface FileService {
    void uploadFile(String client, String command);
    void downloadFile(String client, String command);
    void deleteFile(String client, String command);
    void changeFile(String client, String command);
    void filesList(String client);
}
