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

    private static final String IV = "somerandstr12345";

    public static void main(String args[]) throws Exception {


        //TODO:
//        $keystorePath = '/home/dz-jp-25/Task/';
//        $filename = $keystorePath.'ripple--'.date('c').'--'.'$propose[account_id]';

        String seed = "NPODIWPLKEZPCIIXZVXFIGNLNGVCOIQVLX9Y9COSGAENHSTZKZA9JBTEWIKWQYGRKXDRYMXTWYFAEOOVM";

        String encodedString = "tTpV5UFvJahfMtng8WCNUt0XyJUZxqFaTREngmuylFAYUvJ9tWyZAm6mZgi4bi6RqPwzOGHMv9AVghwjtCu2aDWqB3ZBKdSwP56tWAgGORsOBCu3gyp1LxGtY1IC1ACO=";

        System.out.println("decrypt :" + encodedString + " : " + decrypt(encodedString, IV, IV));

        System.out.println("encrypt = " + new String(Base64.encodeBase64(encrypt(seed, IV, IV))));
    }

    public static byte[] encrypt(String plainText, String key, String iv) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        return cipher.doFinal(plainText.getBytes());
    }

    public static String decrypt(String plainText, String key, String iv) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        return new String(cipher.doFinal(Base64.decodeBase64(plainText)));
    }


    public static String readFileString(final String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    public static byte[] readFileBytes(final String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
