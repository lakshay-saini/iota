package com.example.iota;

import jota.dto.response.GetTransferResponse;
import jota.error.ArgumentException;
import jota.model.Bundle;
import jota.model.Transaction;

import javax.naming.CompositeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Application {

    private static List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) throws ArgumentException {

        IOTACommunicator communicator = new IOTACommunicator(false);

        System.out.println("connecting to node......");

        Bundle[] bundles = communicator.getTransfers().getTransfers();

        Application application = new Application(bundles);


        System.out.println("transfers = " + transactions);
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
