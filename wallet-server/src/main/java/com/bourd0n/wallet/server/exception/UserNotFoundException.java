package com.bourd0n.wallet.server.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super("User with ID '" + userId + "' not found");
    }
}
