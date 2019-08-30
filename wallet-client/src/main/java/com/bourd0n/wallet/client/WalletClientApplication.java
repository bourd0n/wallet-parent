package com.bourd0n.wallet.client;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WalletClientApplication {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

        DepositServiceGrpc.DepositServiceBlockingStub depositStub = DepositServiceGrpc.newBlockingStub(channel);

        depositStub.deposit(MoneyRequest.newBuilder()
                .setAmount(1000)
                .setUserId("userId")
                .setCurrency(CurrencyType.EUR)
                .build());

        WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawStub = WithdrawServiceGrpc.newBlockingStub(channel);

        withdrawStub.withdraw(MoneyRequest.newBuilder()
                .setAmount(100)
                .setUserId("userId")
                .setCurrency(CurrencyType.EUR)
                .build());

        BalanceServiceGrpc.BalanceServiceBlockingStub balanceStub = BalanceServiceGrpc.newBlockingStub(channel);

        BalanceResponse balanceResponse = balanceStub.balance(BalanceRequest.newBuilder()
                .setUserId("userId")
                .build());

        System.out.println(balanceResponse);

        channel.shutdown();
    }
}
