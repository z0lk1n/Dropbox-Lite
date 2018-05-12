import java.util.List;

public class FileMessage extends Message {
    private String fileName;
    private byte[] fileData;
    private List<String> fileList;

    FileMessage(Commands command) {
        super(command);
    }

    FileMessage(Commands command, List<String> fileList) {
        super(command);
        this.fileList = fileList;
    }

    FileMessage(Commands command, String fileName) {
        super(command);
        this.fileName = fileName;
    }

    FileMessage(Commands command, String fileName, byte[] fileData) {
        super(command);
        this.fileName = fileName;
        this.fileData = fileData;
    }

    @Override
    public Commands getCommand() {
        return super.getCommand();
    }

    String getFileName() {
        return fileName;
    }

    byte[] getFileData() {
        return fileData;
    }

    List<String> getFileList() {
        return fileList;
    }
}
