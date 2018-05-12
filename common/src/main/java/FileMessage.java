import java.util.List;

public class FileMessage extends Message {
    private String fileName;
    private byte[] fileData;
    private List<String> fileList;

    public FileMessage(Commands command) {
        super(command);
    }

    public FileMessage(Commands command, List<String> fileList) {
        super(command);
        this.fileList = fileList;
    }

    public FileMessage(Commands command, String fileName) {
        super(command);
        this.fileName = fileName;
    }

    public FileMessage(Commands command, String fileName, byte[] fileData) {
        super(command);
        this.fileName = fileName;
        this.fileData = fileData;
    }

    @Override
    public Commands getCommand() {
        return super.getCommand();
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
