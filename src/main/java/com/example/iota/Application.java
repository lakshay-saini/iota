package com.example.iota;

import jota.error.ArgumentException;

import java.util.Objects;


public class Application {

    public static void main(String[] args) throws ArgumentException {

        IOTACommunicator iotaCommunicator = new IOTACommunicator();

        System.out.println("connecting to node......");

        if(Objects.nonNull(iotaCommunicator.getNodeInfo().getAppName())){
          System.out.println(iotaCommunicator.getNodeInfo());
        }
    }
}
