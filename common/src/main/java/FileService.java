public interface FileService {
    void uploadFile(FileMessage msg);
    void downloadFile(FileMessage msg);
    void deleteFile(FileMessage msg);
    void filesList(FileMessage msg);
}
