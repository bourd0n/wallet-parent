package com.bourd0n.wallet.server.exception;

import com.bourd0n.wallet.api.grpc.CurrencyType;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(long userId, double amount, CurrencyType currency) {
        super("User with ID '" + userId + "' has insufficient funds to get '" + amount + " " + currency + "'");
    }
}
