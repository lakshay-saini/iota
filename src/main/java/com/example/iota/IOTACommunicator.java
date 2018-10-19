package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.*;
import jota.error.ArgumentException;
import jota.model.Bundle;
import jota.model.Input;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * purpose: Establish connection with Iota Node and receive Iota daemon response
 */
class IOTACommunicator {

    private static final String SEED = "NPODIWPLKEZPCIIXZVXFIGNLNGVCOIQVLX9Y9COSGAENHSTZKZA9JBTEWIKWQYGRKXDRYMXTWYFAEOOVM";
    private static final String SEED_I = "NGPYBRC9BBNKDTNRQHLBYZGGTXBDAWDQQSGAMDMZIIQMFXWJNWCDCXEXOEEHBJKSDLHXEKNBWCSZWYBRC";

    private static final String RECIEVER = "JURAUYCCCNOPELURTU9YF9UOKNJSTBZUPUGURZQDJYOUQBKPNHCWDGYNZOPSZFCCGBCCKKBIKLOEPUCSA9IQTVDND9";
    private static final int SECURITY = 2;
    private static final int START = 0;
    private static final int END = 20;
    private static final int THRESHOLD = 100;
    private static final Boolean INCLUSION_STATES = Boolean.TRUE;
    private static final int DEPTH = 5;
    private static final int MIN_WEIGHT_MAGNITUDE = 9;

    private IotaAPI api;
    private int keyIndex;
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
            this.keyIndex = findActiveAddressIndex();
        }

    }

    /**
     * Purpose : Get Account Specific data by providing the private seed
     *
     * @return GetBalancesAndFormatResponse object
     */
    public GetBalancesAndFormatResponse getInputsResponse() {
        GetBalancesAndFormatResponse inputs = new GetBalancesAndFormatResponse();
        try {
            inputs = api.getInputs(SEED, SECURITY, START, END, THRESHOLD);
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return inputs;
    }


    /**
     * purpose : Get the address index which contains account balance
     *
     * @return int
     */
    public int findActiveAddressIndex() {

        return this.inputs.getInputs().stream()
                .filter(input -> Objects.nonNull(input.getKeyIndex())).map(Input::getKeyIndex)
                .collect(Collectors.toList()).get(0);
    }

    /**
     * Purpose : Getting the node account related transfers
     *
     * @return GetTransferResponse Object
     */
    public GetTransferResponse getTransfers() {

        GetTransferResponse transfers = new GetTransferResponse();

        try {
            transfers = api.getTransfers(SEED, SECURITY, 0, 0, INCLUSION_STATES);
        } catch (ArgumentException e) {
            e.printStackTrace();
        }

        return transfers;
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
     *HQRQJKZZVRCKKFUDUCKPGTCBRDNYYJVQHYUXQSAUUYRZWRXYLBBVIWBPKAZBFZLUDVBBBPFHVJUWYQOLC
     * @return List of addresses
     */

    public List<String> getAddresses() {
        List<String> newAddresses = new ArrayList<>();
        try {
            newAddresses = api.getNewAddress(SEED, SECURITY, 0, false, 2, true).getAddresses();
        } catch (ArgumentException e) {
            e.printStackTrace();
        }

        return newAddresses;
    }


    public Boolean setAccount() {

        /*
         * If we can successfully transfer the 0 value to the address then the address get place in tangle
         */
        List<String> addresses = getAddresses();


        try {
            GetBalancesAndFormatResponse inputs = api.getInputs(SEED, 2, 0, 10, 100);


            for (String address : addresses) {
                if(Checksum.isAddressWithChecksum(address)) {
                    address = Checksum.removeChecksum(address);
                    addresses.add(address);
                }
            }


            Transfer transfer = new Transfer(addresses.get(1), 0);
            Transfer transfer1 = new Transfer(addresses.get(1), 40);
            Transfer transfer2 = new Transfer(addresses.get(0), -40);

            List<Transfer> transfers = new ArrayList<>();
            transfers.add(transfer);
            transfers.add(transfer1);

            return api.sendTransfer(getSeed(), SECURITY, 4, 9, transfers, inputs.getInputs(), addresses.get(0), Boolean.TRUE).getSuccessfully()[0];
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
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
            transactionObjects = api.findTransactionObjectsByAddresses(addresses.toArray(new String[addresses.size()]));
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
            accountData = api.getAccountData(SEED, SECURITY, 0, false, 0, true, START, 10, true, 100);
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

    public static void main(String[] args) throws ArgumentException {
        String address = "HQRQJKZZVRCKKFUDUCKPGTCBRDNYYJVQHYUXQSAUUYRZWRXYLBBVIWBPKAZBFZLUDVBBBPFHVJUWYQOLC";
        String addChecksum = Checksum.addChecksum(address);
        System.out.println("addChecksum = " + addChecksum);
    }

    public long getElapsedTime() {
        return this.stopWatch.getElapsedTimeSecs();
    }
}
