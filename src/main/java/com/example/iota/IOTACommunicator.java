package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.GetAccountDataResponse;
import jota.dto.response.GetBalancesAndFormatResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.GetTransferResponse;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Bundle;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.Checksum;
import jota.utils.SeedRandomGenerator;
import jota.utils.StopWatch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * purpose: Establish connection with Iota Node and receive Iota daemon response
 */
class IOTACommunicator {

    private static final String SEED = "NPODIWPLKEZPCIIXZVXFIGNLNGVCOIQVLX9Y9COSGAENHSTZKZA9JBTEWIKWQYGRKXDRYMXTWYFAEOOVM";
    private static final String SEED_I = "NGPYBRC9BBNKDTNRQHLBYZGGTXBDAWDQQSGAMDMZIIQMFXWJNWCDCXEXOEEHBJKSDLHXEKNBWCSZWYBRC";

    private static final String RECIEVER = "JURAUYCCCNOPELURTU9YF9UOKNJSTBZUPUGURZQDJYOUQBKPNHCWDGYNZOPSZFCCGBCCKKBIKLOEPUCSA9IQTVDND9";
    private static final int SECURITY = 2;
    private static final int START = 0;
    private static final int END = 5;
    private static final int THRESHOLD = 1;
    private static final Boolean INCLUSION_STATES = Boolean.TRUE;
    private static final Boolean CHECKSUM = Boolean.FALSE;

    private static final int DEPTH = 4;
    private static final int MIN_WEIGHT_MAGNITUDE = 9;

    private static IotaAPI api;
    private GetNodeInfoResponse nodeInfo;
    private GetBalancesAndFormatResponse inputs;
    private StopWatch stopWatch;

    public IOTACommunicator(Boolean init) {

        this.stopWatch = new StopWatch();

        this.api = new IotaAPI.Builder().protocol("https")
                .host("nodes.devnet.thetangle.org")
                .port("443")
                .build();

        this.nodeInfo = api.getNodeInfo();

        if (init) {
            this.inputs = getInputsResponse();
        }
    }


    public Boolean isNodeActive() {
        return Objects.nonNull(api.getNodeInfo().getLatestMilestone());
    }

    /**
     * Purpose : Get Account Specific data by providing the private seed
     *
     * @return GetBalancesAndFormatResponse object
     */
    public GetBalancesAndFormatResponse getInputsResponse() {
        GetBalancesAndFormatResponse inputs = new GetBalancesAndFormatResponse();
        try {
            if (isNodeActive()) {
                inputs = api.getInputs(SEED, SECURITY, START, END, THRESHOLD);
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return inputs;
    }

    /**
     * Purpose : Getting the node account related transfers
     *
     * @return GetTransferResponse Object
     */
    public GetTransferResponse getTransfers() {

        GetTransferResponse transfers = new GetTransferResponse();

        try {
            if (isNodeActive()) {
                transfers = api.getTransfers(SEED, SECURITY, START, END , INCLUSION_STATES);
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }

        return transfers;
    }

    public Bundle[] getBundleFromAddress(){
        List<String> addresses = getAddresses();
        Bundle[] bundles = new Bundle[0];

        try {
            if (isNodeActive()) {
                bundles = api.bundlesFromAddresses(addresses.toArray(new String[addresses.size()]), true);
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return bundles;
    }

    /**
     * Purpose : Getting account balance
     *
     * @return Long
     */
    public Long getAccountBalance() {
        return this.inputs.getTotalBalance();
    }

    /**
     * Purpose : Getting newly generated address
     * HQRQJKZZVRCKKFUDUCKPGTCBRDNYYJVQHYUXQSAUUYRZWRXYLBBVIWBPKAZBFZLUDVBBBPFHVJUWYQOLC
     *
     * @return List of addresses
     */

    public List<String> getAddresses() {
        List<String> newAddresses = new ArrayList<>();
        try {
            if (isNodeActive()) {
                newAddresses = api.getNewAddress(SEED, SECURITY, START, CHECKSUM, END, true).getAddresses();
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }

        return newAddresses;
    }

    /**
     * Purpose : Method is to transfer iota from one address to another one
     *
     * @param address : String
     * @return : List<Transaction>
     */
    public List<Transaction> sendTransaction(String address) {

        /*
         * If we can successfully transfer the 0 value to the address then the address get place in tangle
         */
        List<String> addresses = getAddresses();

        try {

            List<Transfer> transfers = new ArrayList<>();
            /*
             * Here transfer1 and transfer2 is to secure the balance and transfer to another address
             *  transfer3 is for receiver address
             */

            if(Checksum.isAddressWithChecksum(address)){
                address = Checksum.removeChecksum(address);
            }

            int amount = 5;
            int remainingAccountBalance = getAccountBalance().intValue() - amount;
            String latestAddress = addresses.get(addresses.size() - 1);

            Transfer transfer1 = new Transfer(latestAddress, remainingAccountBalance);
            Transfer transfer2 = new Transfer(latestAddress, 0);
            Transfer transfer3 = new Transfer(address, amount);
            Transfer transfer4 = new Transfer(address, 0);

            transfers.add(transfer1);
            transfers.add(transfer2);
            transfers.add(transfer3);
            transfers.add(transfer4);

            SendTransferResponse sendTransfer = api.sendTransfer(SEED, SECURITY, DEPTH, MIN_WEIGHT_MAGNITUDE, transfers, inputs.getInputs(), addresses.get(0), Boolean.TRUE);

            return sendTransfer.getTransactions();
        } catch (ArgumentException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }


    /**
     * purpose : findTransactionObjects By Addresses related to particular seed
     *
     * @param addresses : List<String>
     * @return : List<Transaction>
     */
    public List<Transaction> findTransactionObjectsByAddresses(List<String> addresses) {
        List<Transaction> transactionObjects = null;
        try {
            if (isNodeActive()) {
                transactionObjects = api.findTransactionObjectsByAddresses(addresses.toArray(new String[addresses.size()]));
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return transactionObjects;
    }

    /**
     * Purpose : Retrieve all data from given seed
     *
     * @return : GetAccountDataResponse object
     */
    public GetAccountDataResponse getAccountData() {
        GetAccountDataResponse accountData = null;
        try {
            if (isNodeActive()) {
                accountData = api.getAccountData(SEED, SECURITY, START, CHECKSUM, END, true, START, END, INCLUSION_STATES, THRESHOLD);
            }
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return accountData;
    }

    /**
     * Purpose : Method to write the content to file
     *
     * @param filename : String
     * @param content  : String
     */
    private static void writeToFile(String filename, String content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Purpose : Method to generate the secure seed
     *
     * @return String
     */
    public static String getSeed() {
        return SeedRandomGenerator.generateNewSeed();
    }

    public long getElapsedTime() {
        return this.stopWatch.getElapsedTimeSecs();
    }
}
