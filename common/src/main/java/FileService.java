public interface FileService {
    void uploadFile(FileMessage msg, String client);

    FileMessage downloadFile(FileMessage msg, String client);

    void deleteFile(FileMessage msg, String client);
}
