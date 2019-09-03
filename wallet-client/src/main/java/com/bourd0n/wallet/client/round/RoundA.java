package com.bourd0n.wallet.client.round;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;

public class RoundA implements Round {

    @Override
    public void makeRound(ManagedChannel channel, long userId) {
        try {
            WalletServiceGrpc.WalletServiceBlockingStub walletService = WalletServiceGrpc.newBlockingStub(channel);

            walletService.deposit(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(200)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            walletService.deposit(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.EUR)
                    .build());

            BalanceResponse balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                    .setUserId(userId)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                    .setUserId(userId)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());
        } catch (Exception e) {
            //todo
            e.printStackTrace();
        }
    }
}
