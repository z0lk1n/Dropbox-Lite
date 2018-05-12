public interface FileService {
    void uploadFile(Object msg);
    void downloadFile(Object msg);
    void deleteFile(Object msg);
    void filesList(Object msg);
}
