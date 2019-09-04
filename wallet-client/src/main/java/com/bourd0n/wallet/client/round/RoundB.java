package com.bourd0n.wallet.client.round;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundB implements Round {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoundB.class);
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
            LOGGER.info("User {}. RoundB finished ok", userId);
        } catch (Exception e) {
            LOGGER.error("User {}. RoundB failed with message: {}", userId, e.getMessage());
        }
    }
}
