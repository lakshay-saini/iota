package com.example.iota;

import jota.error.ArgumentException;
import jota.model.Bundle;
import jota.model.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Application {

    private static List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) throws ArgumentException {

        IOTACommunicator communicator = new IOTACommunicator(true);

        System.out.println("connecting to node......");
        Boolean aBoolean = communicator.setAccount();
        System.out.println("aBoolean = " + aBoolean);
    }

    public Application(Bundle[] bundles) {


        List<List<Transaction>> collectTransactionLists = Arrays
                .stream(bundles)
                .map(Bundle::getTransactions)
                .collect(Collectors.toList());

        for (List<Transaction> collectTransactionList : collectTransactionLists) {
            transactions.addAll(collectTransactionList);
        }

    }
}
