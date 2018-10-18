package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.*;
import jota.error.ArgumentException;
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

    private static final String SEED = "NGPYBRC9BBNKDTNRQHLBYZGGTXBDAWDQQSGAMDMZIIQMFXWJNWCDCXEXOEEHBJKSDLHXEKNBWCSZWYBRC";
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

    public GetBalancesAndFormatResponse getInputs() {
        return inputs;
    }

    private List<Transfer> transfer() {
        Transfer transfer = new Transfer(RECIEVER, 1);
        return Collections.singletonList(transfer);
    }

    public SendTransferResponse sendTransfer() throws ArgumentException {

        StopWatch stopWatch = new StopWatch();
        List<String> trites = api.prepareTransfers(SEED, SECURITY, this.transfer(), StringUtils.EMPTY, this.inputs.getInputs(), Boolean.TRUE);

        List<String> collect = trites.stream().filter(s -> s.length() == 2673).collect(Collectors.toList());

        GetAttachToTangleResponse getAttachToTangleResponse = attachToTangle(collect.get(0), collect.get(1));

        List<Transaction> transactions = api.sendTrytes(collect.toArray(new String[collect.size()]), DEPTH, MIN_WEIGHT_MAGNITUDE);
        Boolean[] successful = new Boolean[transactions.size()];

        for (int i = 0; i < transactions.size(); ++i) {
            FindTransactionResponse response = api.findTransactionsByBundles((transactions.get(i)).getBundle());
            successful[i] = response.getHashes().length != 0;
        }

        return SendTransferResponse.create(transactions, successful, stopWatch.getElapsedTimeMili());

    }

    public GetAttachToTangleResponse attachToTangle(String... collect) throws ArgumentException {
        String trunkTransaction = api.getTips().getHashes()[100];
        String branchTransaction = api.getTips().getHashes()[101];
        return api.attachToTangle(trunkTransaction, branchTransaction, MIN_WEIGHT_MAGNITUDE, collect);

    }

    /**
     * Purpose : Getter method of IotaApi
     *
     * @return IotaApi object
     */
    public IotaAPI getApi() {
        return api;
    }

    /**
     * Purpose : Get the Iota node Info
     *
     * @return GetNodeInfoResponse object
     */
    public GetNodeInfoResponse getNodeInfo() {
        return this.nodeInfo;
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
     *
     * @return List of addresses
     */
    public List<String> getAddresses() {
        List<String> newAddressess = new ArrayList<>();
        try {
            newAddressess = api.getNewAddress(SEED, SECURITY, keyIndex, false, 10, true).getAddresses();
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return newAddressess;
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
