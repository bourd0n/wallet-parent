package com.bourd0n.wallet.client;

import com.bourd0n.wallet.api.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.cli.*;

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

    public static void main(String[] args) {
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
        Integer walletServerPort = Integer.valueOf(cmd.getOptionValue(WALLET_SERVER_PORT.getOpt(), "8081"));
        Integer numberOfUsers = Integer.valueOf(cmd.getOptionValue(NUMBER_OF_USERS.getOpt()));
        Integer numberOfThreads = Integer.valueOf(cmd.getOptionValue(NUMBER_OF_THREADS.getOpt()));
        Integer roundsPerThread = Integer.valueOf(cmd.getOptionValue(ROUNDS_PER_THREAD.getOpt()));

        ManagedChannel channel = ManagedChannelBuilder.forAddress(walletServerHost, walletServerPort)
                .usePlaintext()
                .build();

        UserServiceGrpc.UserServiceBlockingStub userServiceStub = UserServiceGrpc.newBlockingStub(channel);

        CreateUserResponse user = userServiceStub.createUser(CreateUserRequest.newBuilder()
                .putMoneyAmount(CurrencyType.EUR.name(), 100.0)
                .build());

        WalletServiceGrpc.WalletServiceBlockingStub walletService = WalletServiceGrpc.newBlockingStub(channel);

        walletService.deposit(MoneyRequest.newBuilder()
                .setAmount(2000)
                .setUserId(user.getUserId())
                .setCurrency(CurrencyType.EUR)
                .build());

        walletService.withdraw(MoneyRequest.newBuilder()
                .setAmount(500)
                .setUserId(user.getUserId())
                .setCurrency(CurrencyType.EUR)
                .build());

        BalanceResponse balanceResponse = walletService.balance(BalanceRequest.newBuilder()
                .setUserId(user.getUserId())
                .build());

        System.out.println(balanceResponse);

        channel.shutdown();
    }
}
