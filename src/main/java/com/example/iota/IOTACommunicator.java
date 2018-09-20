package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.GetAccountDataResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.error.ArgumentException;

class IOTACommunicator {

    private static IotaAPI api = new IotaAPI.Builder()
            .protocol("http")
            .host("localhost")
            .port("14700")
            .build();


    public GetNodeInfoResponse getNodeInfo() throws ArgumentException {
        return api.getNodeInfo();
    }

    public String getNewAttachedAddress(int lastKnownIndex) throws ArgumentException {
        String seed = IOTASEEDGenerator.getIOTASeed();
        System.out.println("seed = " + seed);
        return api.getNewAddress(seed, 2, lastKnownIndex, true, 1, true).getAddresses().get(0);
    }

    public GetAccountDataResponse getAccountData(String seed) throws ArgumentException {
        return api.getAccountData(seed, 2, 0, true, 1, true,1, 10, true, 100);
    }
}
