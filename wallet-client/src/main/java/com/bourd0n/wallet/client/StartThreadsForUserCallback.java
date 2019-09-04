package com.bourd0n.wallet.client;

import com.bourd0n.wallet.api.grpc.CreateUserResponse;
import com.bourd0n.wallet.client.round.RoundB;
import com.google.common.util.concurrent.FutureCallback;
import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StartThreadsForUserCallback implements FutureCallback<CreateUserResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoundB.class);

    private final ExecutorService executorService;
    private final int numberOfThreads;
    private final int roundsPerThread;
    private final ManagedChannel channel;
    private final CountDownLatch countDownLatch;

    public StartThreadsForUserCallback(int numberOfThreads, int roundsPerThread,
                                       ExecutorService executorService, ManagedChannel channel,
                                       CountDownLatch countDownLatch) {
        this.executorService = executorService;
        this.numberOfThreads = numberOfThreads;
        this.roundsPerThread = roundsPerThread;
        this.channel = channel;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onSuccess(@Nullable CreateUserResponse result) {
        if (result != null) {
            long userId = result.getUserId();
            LOGGER.info("Start {} threads for user {}", numberOfThreads, userId);
            List<CompletableFuture<Void>> futures =
                    IntStream.rangeClosed(1, numberOfThreads)
                    .mapToObj((i) -> CompletableFuture.supplyAsync(new UserRoundsAction(userId, roundsPerThread, channel), executorService))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
            LOGGER.info("{} threads for user {} are finished", numberOfThreads, userId);
            countDownLatch.countDown();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        //todo
        t.printStackTrace();
    }
}
