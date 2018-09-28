package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.GetAccountDataResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.error.ArgumentException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

class IOTACommunicator {

    private  IotaAPI api ;

    private static final String TRYTE_ALPHABET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SEED_LEN = 81;

    public IOTACommunicator(String port) {
        this.api = new IotaAPI.Builder().protocol("http")
                .host("localhost")
                .port(port)
                .build();
    }

    public GetNodeInfoResponse getNodeInfo() throws ArgumentException {
        writeToFile("node.info", api.getNodeInfo().toString());
        return api.getNodeInfo();
    }

    public String getNewAttachedAddress(int lastKnownIndex) throws ArgumentException {
        String seed = getIOTASeed();
        writeToFile("seed.txt", seed);
        return api.getNewAddress(seed, 2, lastKnownIndex, true, 1, true).getAddresses().get(0);
    }

    public GetAccountDataResponse getAccountData(String seed) throws ArgumentException {
        return api.getAccountData(seed, 2, 0, true, 1, true,1, 10, true, 100);
    }

    public static String getIOTASeed(){

        Random random = new Random();
        // the resulting seed
        StringBuilder seed = new StringBuilder(SEED_LEN);
        for(int i = 0; i < SEED_LEN; i++) {
            int n = random.nextInt(27);
            char c = TRYTE_ALPHABET.charAt(n);
            seed.append(c);
        }

        return seed.toString();
    }

    private static void writeToFile(String filename, String content){
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
