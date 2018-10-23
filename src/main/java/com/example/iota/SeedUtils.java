package com.example.iota;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SeedUtils {

    public static void main(String args[]) throws Exception {

        String value = "somerandstr12345";

        String PhpEncodedString = "9ud86wfgllkbiJ0VBcwrhAvnWnBB8eBoTjTMiaMlOKQ=";
        String javaEncodedString = "lB4zuOZyA2LWJ6RYyca5Ghx0HYd6psDb27H51DLKQAc=";

        String key = "somerandstr12345";
        String iv = "somerandstr12345";

        System.out.println("decrypt :" + PhpEncodedString + " : " + decrypt(PhpEncodedString, key, iv));
        System.out.println("decrypt :" + javaEncodedString + " = " + decrypt(javaEncodedString, key, iv));

        System.out.println("encrypt = " + new String(Base64.encodeBase64(encrypt(value, key, iv))));
    }

    public static byte[] encrypt(String  plainText, String key, String iv) throws Exception{

        byte[] textBytes = plainText.getBytes();
        byte[] keyBytes = key.getBytes();
        byte[] ivBytes  = iv.getBytes();

        SecretKeySpec secretKey=new SecretKeySpec(keyBytes,"AES");
        IvParameterSpec ivParameterSpec=new IvParameterSpec(ivBytes);
        Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivParameterSpec);

        return cipher.doFinal(textBytes);
    }

    public static String decrypt(String  plainText, String key, String iv) throws Exception{

        byte[] textBytes = Base64.decodeBase64(plainText);
        byte[] keyBytes = key.getBytes();
        byte[] ivBytes  = iv.getBytes();

        SecretKeySpec secretKey=new SecretKeySpec(keyBytes,"AES");
        IvParameterSpec ivParameterSpec=new IvParameterSpec(ivBytes);
        Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,secretKey,ivParameterSpec);

        return new String(cipher.doFinal(textBytes));
    }


    public static String readFileString(final String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    public static byte[] readFileBytes(final String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
