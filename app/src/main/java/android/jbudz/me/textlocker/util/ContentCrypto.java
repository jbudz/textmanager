package android.jbudz.me.textlocker.util;


import android.util.Base64;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jon on 7/5/14.
 */
final public class ContentCrypto {
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 10;
    private static String mSeparator = "]";

    /**
     *
     * @param passphrase
     * @param salt
     * @return a generated secret key
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.spec.InvalidKeySpecException
     */
    private static SecretKey generateSecret(char[] passphrase, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

    public static String encrypt(String content, byte[] salt, char[] passPhrase) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        try {
            SecretKey secret = generateSecret(passPhrase, salt);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(content.getBytes("UTF-8"));
            return Base64.encodeToString(iv, 0) + mSeparator + Base64.encodeToString(ciphertext, 0);
        } catch(Exception e) {
            throw new SecurityException();
        }
    }


    public static String decrypt(String content, byte[] salt, char[] passPhrase) throws Exception {
        if (content == null || content.isEmpty()) {
            return "";
        }

        int separatorIndex = content.indexOf(mSeparator);
        byte[] iv = Base64.decode(content.substring(0, separatorIndex), 0);
        byte[] ciphertext = Base64.decode(content.substring(separatorIndex + mSeparator.length()), 0);

        SecretKey secret = generateSecret(passPhrase, salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
        return plaintext;
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes;
    }
}
