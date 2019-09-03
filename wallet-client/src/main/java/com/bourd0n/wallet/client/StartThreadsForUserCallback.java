package com.bourd0n.wallet.client;

import com.bourd0n.wallet.api.grpc.CreateUserResponse;
import com.google.common.util.concurrent.FutureCallback;
import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StartThreadsForUserCallback implements FutureCallback<CreateUserResponse> {

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
            List<CompletableFuture<Void>> futures =
                    IntStream.rangeClosed(1, numberOfThreads)
                    .mapToObj((i) -> CompletableFuture.supplyAsync(new UserRoundsAction(userId, roundsPerThread, channel), executorService))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
            countDownLatch.countDown();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        //todo
        t.printStackTrace();
    }
}
