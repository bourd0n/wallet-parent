package com.bourd0n.wallet.client.round;

import io.grpc.ManagedChannel;

public interface Round {
    void makeRound(ManagedChannel channel, long userId);
}
