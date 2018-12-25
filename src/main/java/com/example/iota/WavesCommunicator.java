package com.example.iota;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.transactions.TransferTransaction;
import com.wavesplatform.wavesj.transactions.TransferTransactionV2;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class WavesCommunicator {

        private static BigDecimal WEI = new BigDecimal("100000000");
    public static void main(String[] args) throws IOException {
        String seed = "either salt enough lounge hawk labor during coin nasty enrich corn salute essay squirrel rookie";
        PrivateKeyAccount alice = PrivateKeyAccount.fromSeed(seed, 0, Account.TESTNET);
        // Retrieve its public key
        byte[] publicKey = alice.getPublicKey();
        // and its address
        String address = alice.getAddress();

        // Create a Node ("https://testnode1.wavesnodes.com" by default, or you can pass another URL here)
        Node node = new Node();
        long balance = node.getBalance(address, Asset.WAVES);
        System.out.println("balance = " + balance);

        Block block = node.getBlock(420423);
        Collection<Transaction> transactions = block.getTransactions();

        for (Transaction transaction : transactions) {
            if(transaction.getClass().equals(TransferTransactionV2.class)) {
                TransferTransaction transferTransaction = (TransferTransaction) transaction;
                BigDecimal amount = BigDecimal.valueOf(transferTransaction.getAmount()).divide(WEI, 5, BigDecimal.ROUND_UP);
                System.out.println("address : amount = " + transferTransaction.getRecipient() + ":" + amount);
            }
        }
    }
}
