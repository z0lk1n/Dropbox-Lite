import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BaseFileService implements FileService, Const {
    @Override
    public void uploadFile(FileMessage msg, String client) {
        try {
            Files.write(Paths.get(Const.CORE_PATH + client + "/" + msg.getFileName()), msg.getFileData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileMessage downloadFile(FileMessage msg, String client) {
        String fileName = msg.getFileName();
        byte[] fileData = null;
        try {
            fileData = Files.readAllBytes(Paths.get(Const.CORE_PATH + client + "/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileMessage(Commands.DOWNLOAD_FILE, fileName, fileData);
    }

    @Override
    public void deleteFile(FileMessage msg, String client) {
        try {
            Files.delete(Paths.get(Const.CORE_PATH + client + "/" + msg.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
