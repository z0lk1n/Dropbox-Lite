import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class BaseFileService implements FileService, Const {
    @Override
    public void uploadFile(Object msg) {

    }

    @Override
    public void downloadFile(Object msg) {

    }

    @Override
    public void deleteFile(Object msg) {

    }

    @Override
    public void filesList(Object msg) {

    }

    static String getHash(String password) {
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

        assert messageDigest != null;
        byte[] saltedHash = messageDigest.digest(saltPassword);

        Base64.Encoder base64Encoder = Base64.getEncoder();

        return base64Encoder.encodeToString(saltedHash);
    }
}
