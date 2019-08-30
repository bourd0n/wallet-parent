package com.bourd0n.wallet.client;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WalletClientApplication {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

        UserServiceGrpc.UserServiceBlockingStub userServiceStub = UserServiceGrpc.newBlockingStub(channel);

        CreateUserResponse user = userServiceStub.createUser(CreateUserRequest.newBuilder()
                .putMoneyAmount(CurrencyType.EUR.name(), 100.0)
                .build());

        DepositServiceGrpc.DepositServiceBlockingStub depositStub = DepositServiceGrpc.newBlockingStub(channel);

        depositStub.deposit(MoneyRequest.newBuilder()
                .setAmount(2000)
                .setUserId(user.getUserId())
                .setCurrency(CurrencyType.EUR)
                .build());

        WithdrawServiceGrpc.WithdrawServiceBlockingStub withdrawStub = WithdrawServiceGrpc.newBlockingStub(channel);

        withdrawStub.withdraw(MoneyRequest.newBuilder()
                .setAmount(500)
                .setUserId(user.getUserId())
                .setCurrency(CurrencyType.EUR)
                .build());

        BalanceServiceGrpc.BalanceServiceBlockingStub balanceStub = BalanceServiceGrpc.newBlockingStub(channel);

        BalanceResponse balanceResponse = balanceStub.balance(BalanceRequest.newBuilder()
                .setUserId(user.getUserId())
                .build());

        System.out.println(balanceResponse);

        channel.shutdown();
    }
}
