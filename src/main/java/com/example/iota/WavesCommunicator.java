package com.example.iota;

import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.util.Collection;

public class WavesCommunicator {
    public static void main(String[] args) throws IOException {
        String seed = "either salt enough lounge hawk labor during coin nasty enrich corn salute essay squirrel rookie";
        PrivateKeyAccount alice = PrivateKeyAccount.fromSeed(seed, 0, Account.TESTNET);
        // Retrieve its public key
        byte[] publicKey = alice.getPublicKey();
        // and its address
        String address = alice.getAddress();

        // Create a Node ("https://testnode1.wavesnodes.com" by default, or you can pass another URL here)
        Node node = new Node();
        Block block = node.getBlock(419087);
        Collection<Transaction> transactions = block.getTransactions();
        int height = node.getHeight();
        System.out.println("height: " + height);

        transactions.forEach(System.out::println);
        // Get blockchain height
//        int height = node.getHeight();
//        System.out.println("height: " + height);

        // Learn address balance
//        System.out.println("my's balance: " + node.getBalance(address));
    }
}
