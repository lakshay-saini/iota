package com.example.iota;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SeedUtils {

    static byte [] deriveKeyAndIV(byte[] password, byte[] salt) throws
            DigestException, NoSuchAlgorithmException {

        byte[] res = new byte[48];

        final MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update(password);
        md5.update(salt);
        byte[] hash1 = md5.digest();

        md5.reset();
        md5.update(hash1);
        md5.update(password);
        md5.update(salt);
        byte[] hash2 = md5.digest();

        md5.reset();
        md5.update(hash2);
        md5.update(password);
        md5.update(salt);
        byte[] hash3 = md5.digest();

        // copy the 3 hashes in the result array
        System.arraycopy(hash1, 0, res, 0, 16);
        System.arraycopy(hash2, 0, res, 16, 16);
        System.arraycopy(hash3, 0, res, 32, 16);
        return res;
    }

    /*
     * Decrypt the string data, using the password secret,
     * in an openssl-compatible way. This function provides the equivalent
     * of the command:
     *
     * echo <data> | openssl aes-256-cbc -d -base64 -pass pass:<secret>
     */
    public static String aesDecrypt(String secret, String data)
            throws DigestException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {
        byte[] decoded = Base64.decodeBase64(data);
        byte[] salt = Arrays.copyOfRange(decoded, 8, 16);
        byte[] encrypted = Arrays.copyOfRange(decoded, 16, decoded.length);

        byte[] keyAndIV = deriveKeyAndIV(secret.getBytes(), salt);
        byte[] key = Arrays.copyOfRange(keyAndIV, 0, 32);
        byte[] iv = Arrays.copyOfRange(keyAndIV, 32, 48);

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher cipher;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
        return new String(cipher.doFinal(encrypted));
    }

    /*
     * Encrypt the string data, using the password secret.
     * This function provides the equivalent of the command:
     *
     * echo <data> | openssl aes-256-cbc -base64 -pass pass:<secret>
     */
    public static String aesEncrypt(String secret, String data)
            throws DigestException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        byte [] salt = new byte [8];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);

        byte [] keyAndIV = deriveKeyAndIV(secret.getBytes(), salt);
        byte [] key = Arrays.copyOfRange(keyAndIV, 0, 32);
        byte [] iv = Arrays.copyOfRange(keyAndIV, 32, 48);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
        byte [] encrypted = cipher.doFinal(data.getBytes());

        byte [] to_encode = new byte[ 16 + encrypted.length ];
        System.arraycopy("Salted__".getBytes(), 0, to_encode, 0, 8);
        System.arraycopy(salt, 0, to_encode, 8, 8);
        System.arraycopy(encrypted, 0, to_encode, 16, encrypted.length);
        return new String(Base64.encodeBase64(to_encode));
    }

    public static void main(String args[]) throws DigestException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        // obtained with:
        // echo -n "original string" | openssl aes-256-cbc -base64 -pass pass:secret
        String enc = "6egwKkelFpEh0PSTvWl9tQ==";

//        System.out.println(aesDecrypt("lakshay", "6egwKkelFpEh0PSTvWl9tQ=="));
          System.out.println(aesEncrypt("lakshay", "lakshay"));

    }


    public static String readFileString(final String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    public static byte[] readFileBytes(final String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
