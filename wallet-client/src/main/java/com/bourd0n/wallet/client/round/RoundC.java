package com.bourd0n.wallet.client.round;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundC implements Round {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoundB.class);

    @Override
    public void makeRound(ManagedChannel channel, long userId) {
        try {
            WalletServiceGrpc.WalletServiceBlockingStub walletService = WalletServiceGrpc.newBlockingStub(channel);

            BalanceResponse balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                    .setUserId(userId)
                    .build());

            walletService.deposit(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            walletService.deposit(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            walletService.deposit(MoneyRequest.newBuilder()
                    .setAmount(100)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                    .setUserId(userId)
                    .build());

            walletService.withdraw(MoneyRequest.newBuilder()
                    .setAmount(200)
                    .setUserId(userId)
                    .setCurrency(CurrencyType.USD)
                    .build());

            balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                    .setUserId(userId)
                    .build());
            LOGGER.info("User {}. RoundC finished ok", userId);
        } catch (Exception e) {
            LOGGER.error("User {}. RoundC failed with message: {}", userId, e.getMessage());
        }
    }
}
