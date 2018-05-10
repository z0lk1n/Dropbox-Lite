public class Message {
    private String command;
    private String client;
    private String fileName;
    private byte[] fileData;

    public Message(String command) {
        this.command = command;
    }

    public Message(String command, String client) {
        this.command = command;
        this.client = client;
    }

    public Message(String command, String client, String fileName) {
        this.command = command;
        this.client = client;
        this.fileName = fileName;
    }

    public Message(String command, String client, String fileName, byte[] fileData) {
        this.command = command;
        this.client = client;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getCommand() {
        return command;
    }

    public String getClient() {
        return client;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }
}

