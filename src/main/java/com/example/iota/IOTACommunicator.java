package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;
import jota.error.ArgumentException;
import jota.utils.SeedRandomGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

class IOTACommunicator {

    private  IotaAPI api ;

    private static final String TRYTE_ALPHABET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SEED_LEN = 81;


    public IotaAPI getApi(){
        return api;
    }

    public IOTACommunicator() {
        this.api = new IotaAPI.Builder().protocol("http")
                .host("192.168.0.168")
                .port("14265")
                .build();
    }

    public GetNodeInfoResponse getNodeInfo(){
        writeToFile("node.info", api.getNodeInfo().toString());
        return api.getNodeInfo();
    }

    public String getNewAttachedAddress() throws ArgumentException {
        String seed = getIOTASeed();
        writeToFile("seed.txt", seed);
        return api.getNewAddress(seed, 1, 0, false, 2, true).getAddresses().get(0);
    }

    public static void main(String[] args) throws ArgumentException {
        IOTACommunicator iotaCommunicator = new IOTACommunicator();
        System.out.println("iotaCommunicator = " + iotaCommunicator.getNewAttachedAddress());
    }

    public String[] getTips(){
        return api.getTips().getHashes();
    }


    public static String getIOTASeed(){
        return SeedRandomGenerator.generateNewSeed();
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
