public interface FileService {
    void uploadFile(Message msg);
    void downloadFile(Message msg);
    void deleteFile(Message msg);
    void changeFile(Message msg);
    void filesList(Message msg);
}
