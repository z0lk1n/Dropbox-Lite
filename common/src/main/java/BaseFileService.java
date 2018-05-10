import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class BaseFileService implements FileService, Const {
    @Override
    public void uploadFile(FileMessage msg) {
        String client = msg.getClient();
        String fileName = msg.getFileName();
        byte[] fileData = msg.getFileData();
        try {
            Files.createDirectories(Paths.get(Const.CORE_PATH + client));
            Files.write(Paths.get(Const.CORE_PATH + client + "/" + fileName), fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadFile(FileMessage msg) {
        String client = msg.getClient();
        String fileName = msg.getFileName();
        byte[] fileData = null;
        try {
            fileData = Files.readAllBytes(Paths.get(Const.CORE_PATH + client + "/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFile(FileMessage msg) {
        String client = msg.getClient();
        String fileName = msg.getFileName();
        try {
            Files.delete(Paths.get(Const.CORE_PATH + client + "/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void filesList(FileMessage msg) {

    }

    public static String getHash(String password) {
        String algorithm = "SHA-256";
        String salt = "salt";

        byte[] passwordArr = password.getBytes();
        byte[] saltArr = salt.getBytes();

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unknown algorithm");
        }

        int len1 = saltArr.length;
        int len2 = passwordArr.length;

        byte[] saltPassword = new byte[len1 + len2];

        System.arraycopy(saltArr, 0, saltPassword, 0, len1);
        System.arraycopy(passwordArr, 0, saltPassword, len1, len2);

        byte[] saltedHash = messageDigest.digest(saltPassword);

        Base64.Encoder base64Encoder = Base64.getEncoder();

        return base64Encoder.encodeToString(saltedHash);
    }
}
