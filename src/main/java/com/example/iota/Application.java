package com.example.iota;

import jota.IotaAPI;
import jota.dto.response.GetAttachToTangleResponse;
import jota.dto.response.GetBalancesAndFormatResponse;
import jota.dto.response.GetTransactionsToApproveResponse;
import jota.dto.response.GetTransferResponse;
import jota.error.ArgumentException;
import jota.model.Neighbor;

import java.util.List;
import java.util.Objects;


public class Application {

    public static void main(String[] args) throws ArgumentException {

        IOTACommunicator iotaCommunicator = new IOTACommunicator();

        String seed = "NGPYBUC9BBNKDTNRQHLBYZGGTXBDAWDQQSGAMDMZIIQMFXWJNWCDCXEXOEEHBJKSDLHXEKNBWCSZWYBRC";

        System.out.println("connecting to node......");

        if(Objects.nonNull(iotaCommunicator.getNodeInfo())){

            System.out.println("iotaCommunicator = " + iotaCommunicator.getNodeInfo());

            IotaAPI api = iotaCommunicator.getApi();
            String[] trytes = api.getTrytes(api.getTips().getHashes()).getTrytes();
            int security = 1;
            int index = 0;
            long thresold = 100;
            GetTransferResponse transfers = api.getTransfers(seed, 1, 1, 10, false);
            api.getNewAddress(seed, security, index, false, 10, true).getAddresses().stream().forEach(System.out::println);
            GetAttachToTangleResponse getAttachToTangleResponse = api.attachToTangle(api.getTips().getHashes()[0], api.getTips().getHashes()[1], 9, api.getTrytes(api.getTips().getHashes()[0]).getTrytes());
//            GetAccountDataResponse accountData = api.getAccountData();
            String[] addressess = {"VDKQ9EIHF9PIHHKNIZTXJKCBAIGKDOYEAJUZRBNGGWNURNLBRDKNGUXUKKLGFSGXMUXPQFCDSJFDGSKSAJQKBVGBBD"};


            GetTransactionsToApproveResponse transactionsToApprove = api.getTransactionsToApprove(9);
            System.out.println("transactionsToApprove = " + transactionsToApprove);


            //            List<Transfer> transfers = new ArrayList<>();
//            transfers.add(new Transfer("ZBVPGOVSBZHPBQVQVXAFZBMEDLAMAZQXMISGZFIE9UZHOXAAKPWXV9CVPNPYPOEHBTYJEFJIHQXCUEFHDZPGMTYKYW", 0));

//            GetBalancesResponse balances = api.getBalances(100, Collections.singletonList("VDKQ9EIHF9PIHHKNIZTXJKCBAIGKDOYEAJUZRBNGGWNURNLBRDKNGUXUKKLGFSGXMUXPQFCDSJFDGSKSAJQKBVGBBD"));
//            System.out.println("balances = " + balances);

            List<Neighbor> neighbors = api.getNeighbors().getNeighbors();

            System.out.println("neighbors = " + neighbors);
            api.attachToTangle(api.getTips().getHashes()[0], api.getTips().getHashes()[1], 9, api.getTrytes(api.getTips().getHashes()[0]).getTrytes());

            GetBalancesAndFormatResponse inputs = api.getInputs(seed, 1, 0, 10, 10);
            System.out.println("inputs = " + inputs);

//            List<String> addresses = api.getAccountData(seed, 1, 0, false, 10, true, 0, 10, true, 10).getAddresses();
//            GetBalancesResponse balances = api.getBalances(10, addresses);

//            System.out.println(balances.getBalances());

//            GetBalancesAndFormatResponse inputs = api.getInputs(seed, 1, 0, 10, 10);
//            SendTransferResponse sendTransfer = api.sendTransfer(seed, 2, 4, 9, transfers, inputs.getInputs(), null, true);
//            List<Transaction> transactions = sendTransfer.getTransactions();
//            Boolean[] successfully = sendTransfer.getSuccessfully();
//
//            System.out.println("transactions = " + transactions);
//            System.out.println("successfully = " + successfully);


//            String[] addressess = {"VDKQ9EIHF9PIHHKNIZTXJKCBAIGKDOYEAJUZRBNGGWNURNLBRDKNGUXUKKLGFSGXMUXPQFCDSJFDGSKSAJQKBVGBBD"};
//            List<Transaction> addresses = api.findTransactionObjectsByAddresses(addressess);
//            System.out.println("addresses = " + addresses);

//            String[] strings = {"SUQXXQWPVDUWZRHIOPCQEBTBJFXHAMVFNQFXROTZXWGIOSLPQJQGJMETXQGFBPIYNFUCWIXKCD9G99999"};
//            List<Transaction> transactionsObjectsByHashes = api.findTransactionsObjectsByHashes(strings);
//            System.out.println("transactionsObjectsByHashes = " + transactionsObjectsByHashes);

        }
    }
}
