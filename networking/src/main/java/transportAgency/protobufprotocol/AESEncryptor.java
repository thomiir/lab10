package transportAgency.protobufprotocol;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESEncryptor {
    private static final String KEY_B64 = "b14ca5898a4e4133bbce2ea2315a1916";
    private static final String IV_B64 = "Kk7V9e3fQw2p1lzT";

    private static byte[] getValidKey() {
        StringBuilder paddedKey = new StringBuilder(KEY_B64);
        while (paddedKey.length() < 32) {
            paddedKey.append("0");
        }
        return paddedKey.toString().getBytes();
    }

    private static byte[] getValidIV() {
        StringBuilder paddedIV = new StringBuilder(IV_B64);
        while (paddedIV.length() < 16) {
            paddedIV.append("0");
        }
        return paddedIV.toString().getBytes();
    }

    public static String encrypt(String plaintext) throws Exception {
        byte[] key = getValidKey();
        byte[] iv = getValidIV();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
}