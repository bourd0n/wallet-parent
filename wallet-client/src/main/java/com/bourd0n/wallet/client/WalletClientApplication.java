package com.bourd0n.wallet.client;

import com.bourd0n.wallet.api.grpc.CreateUserRequest;
import com.bourd0n.wallet.api.grpc.CurrencyType;
import com.bourd0n.wallet.api.grpc.UserServiceGrpc;
import com.google.common.util.concurrent.Futures;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.cli.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

//todo: add logging
public class WalletClientApplication {

    private static final Options OPTIONS = new Options();

    private static final Option WALLET_SERVER_HOST = Option.builder("h")
            .longOpt("walletServerHost")
            .hasArg()
            .desc("Wallet server host. By default: localhost")
            .required(false)
            .build();

    private static final Option WALLET_SERVER_PORT = Option.builder("p")
            .longOpt("walletServerPort")
            .type(Integer.class)
            .hasArg()
            .desc("Wallet server port. By default: 8081")
            .required(false)
            .build();

    private static final Option NUMBER_OF_USERS = Option.builder("u")
            .longOpt("usersCount")
            .type(Integer.class)
            .hasArg()
            .desc("Number of concurrent users emulated")
            .required()
            .build();

    private static final Option NUMBER_OF_THREADS = Option.builder("t")
            .longOpt("threadCount")
            .type(Integer.class)
            .hasArg()
            .desc("Number of concurrent requests a user will make")
            .required()
            .build();

    private static final Option ROUNDS_PER_THREAD = Option.builder("r")
            .longOpt("roundsPerThread")
            .type(Integer.class)
            .hasArg()
            .desc("Number of rounds each thread is executing")
            .required()
            .build();

    static {
        OPTIONS.addOption(WALLET_SERVER_HOST);
        OPTIONS.addOption(WALLET_SERVER_PORT);
        OPTIONS.addOption(NUMBER_OF_USERS);
        OPTIONS.addOption(NUMBER_OF_THREADS);
        OPTIONS.addOption(ROUNDS_PER_THREAD);
    }

    public static void main(String[] args) throws InterruptedException {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e);
            formatter.printHelp("wallet-client", OPTIONS);
            System.exit(1);
        }

        //todo: typesafety
        String walletServerHost = cmd.getOptionValue(WALLET_SERVER_HOST.getOpt(), "localhost");
        int walletServerPort = Integer.parseInt(cmd.getOptionValue(WALLET_SERVER_PORT.getOpt(), "8081"));
        int numberOfUsers = Integer.parseInt(cmd.getOptionValue(NUMBER_OF_USERS.getOpt()));
        int numberOfThreads = Integer.parseInt(cmd.getOptionValue(NUMBER_OF_THREADS.getOpt()));
        int roundsPerThread = Integer.parseInt(cmd.getOptionValue(ROUNDS_PER_THREAD.getOpt()));

        ManagedChannel channel = ManagedChannelBuilder.forAddress(walletServerHost, walletServerPort)
                .usePlaintext()
                .build();

        UserServiceGrpc.UserServiceFutureStub userServiceStub = UserServiceGrpc.newFutureStub(channel);

        CreateUserRequest createUserRequest = CreateUserRequest.newBuilder()
                .putMoneyAmount(CurrencyType.EUR.name(), 0.0)
                .putMoneyAmount(CurrencyType.USD.name(), 0.0)
                .putMoneyAmount(CurrencyType.GBP.name(), 0.0)
                .build();

        //todo: init
        ExecutorService executorService = Executors.newFixedThreadPool((numberOfThreads + 1) * numberOfUsers);

        CountDownLatch countDownLatch = new CountDownLatch(numberOfUsers);
        IntStream.rangeClosed(1, numberOfUsers)
                .mapToObj((i) -> userServiceStub.createUser(createUserRequest))
                .forEach(f -> Futures.addCallback(f,
                        new StartThreadsForUserCallback(numberOfThreads, roundsPerThread, executorService, channel, countDownLatch),
                        executorService));

        countDownLatch.await();
        channel.shutdown();
    }
}
