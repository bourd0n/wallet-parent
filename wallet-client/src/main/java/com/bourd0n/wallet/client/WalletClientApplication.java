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

        WalletServiceGrpc.WalletServiceBlockingStub walletService = WalletServiceGrpc.newBlockingStub(channel);

        walletService.deposit(MoneyRequest.newBuilder()
                .setAmount(2000)
                .setUserId(user.getUserId())
                .setCurrency(CurrencyType.EUR)
                .build());

        walletService.withdraw(MoneyRequest.newBuilder()
                .setAmount(500)
                .setUserId(user.getUserId())
                .setCurrency(CurrencyType.EUR)
                .build());

        BalanceResponse balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                .setUserId(user.getUserId())
                .build());

        System.out.println(balanceResponse);

        channel.shutdown();
    }
}
