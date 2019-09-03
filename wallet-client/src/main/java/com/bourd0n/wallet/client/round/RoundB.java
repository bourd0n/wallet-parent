package com.bourd0n.wallet.client.round;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;

public class RoundB implements Round {
    @Override
    public void makeRound(ManagedChannel channel, long userId) {
        try {
            WalletServiceGrpc.WalletServiceBlockingStub walletService = WalletServiceGrpc.newBlockingStub(channel);

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.GBP)
                    .build());

            walletService.deposit(MoneyRequest.newBuilder()
                    .setAmount(300)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.GBP)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.GBP)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.GBP)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.GBP)
                    .build());
        } catch (Exception e) {
            //todo
            e.printStackTrace();
        }
    }
}
