import java.util.List;

public class FileMessage extends Message {
    private String fileName;
    private byte[] fileData;
    private List<String> fileList;

    public FileMessage(Commands command, String client) {
        super(command, client);
    }

    public FileMessage(Commands command, String client, String fileName) {
        super(command, client);
        this.fileName = fileName;
    }

    public FileMessage(Commands command, String client, List<String> fileList) {
        super(command, client);
        this.fileList = fileList;
    }

    public FileMessage(Commands command, String client, String fileName, byte[] fileData) {
        super(command, client);
        this.fileName = fileName;
        this.fileData = fileData;
    }

    @Override
    public Commands getCommand() {
        return super.getCommand();
    }

    @Override
    public String getClient() {
        return super.getClient();
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public List<String> getFileList() {
        return fileList;
    }
}
