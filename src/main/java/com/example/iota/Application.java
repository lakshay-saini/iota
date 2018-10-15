package com.example.iota;

import jota.dto.response.GetTransferResponse;
import jota.error.ArgumentException;


public class Application {

    public static void main(String[] args) throws ArgumentException {

        IOTACommunicator iotaCommunicator = new IOTACommunicator(true);

        System.out.println("connecting to node......");


        //GetBalancesAndFormatResponse inputs = iotaCommunicator.getInputs();

        GetTransferResponse transfers = iotaCommunicator.getTransfers();
        System.out.println("inputs = " + transfers);


    }
}
