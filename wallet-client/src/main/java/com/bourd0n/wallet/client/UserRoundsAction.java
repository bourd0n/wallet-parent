package com.bourd0n.wallet.client;

import com.bourd0n.wallet.client.round.Round;
import com.bourd0n.wallet.client.round.RoundA;
import com.bourd0n.wallet.client.round.RoundB;
import com.bourd0n.wallet.client.round.RoundC;
import com.google.common.collect.Lists;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class UserRoundsAction implements Supplier<Void> {

    private final List<Round> availableRounds;
    private final long userId;
    private final int roundsPerThread;
    private final Random random = new Random();
    private final ManagedChannel channel;

    public UserRoundsAction(long userId, int roundsPerThread, ManagedChannel channel) {
        this.userId = userId;
        this.roundsPerThread = roundsPerThread;
        this.channel = channel;
        this.availableRounds = Lists.newArrayList(new RoundA(), new RoundB(), new RoundC());
    }

    @Override
    public Void get() {
        for (int i = 0; i < roundsPerThread; i++) {
            Round round = availableRounds.get(random.nextInt(availableRounds.size()));
            round.makeRound(channel, userId);
        }
        return null;
    }
}
