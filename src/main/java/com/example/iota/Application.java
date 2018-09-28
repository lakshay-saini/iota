package com.example.iota;

import jota.error.ArgumentException;

import java.util.Objects;


public class Application {

    public static void main(String[] args) throws ArgumentException {

        IOTACommunicator iotaCommunicator = new IOTACommunicator("14700");

        System.out.println("connecting to node......");

        if(Objects.nonNull(iotaCommunicator.getNodeInfo())){
          System.out.println(iotaCommunicator.getNodeInfo());
        }
    }
}
