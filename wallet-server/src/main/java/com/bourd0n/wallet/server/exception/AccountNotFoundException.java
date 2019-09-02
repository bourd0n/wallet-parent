package com.bourd0n.wallet.server.exception;

import com.bourd0n.wallet.api.grpc.CurrencyType;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long userId, CurrencyType currency) {
        super("User with ID '" + userId + "' have no '" + currency + "' account");
    }
}
