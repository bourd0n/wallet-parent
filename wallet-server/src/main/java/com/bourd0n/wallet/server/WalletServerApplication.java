package com.bourd0n.wallet.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class WalletServerApplication implements CommandLineRunner {

    private final List<BindableService> grpcServices;
    private final int grpcPort;

    @Autowired
    public WalletServerApplication(List<BindableService> grpcServices, @Value("${grpc.port}") int grpcPort) {
        this.grpcServices = grpcServices;
        this.grpcPort = grpcPort;
    }

    public static void main(String[] args) {
        SpringApplication.run(WalletServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ServerBuilder<?> serverBuilder = ServerBuilder
                .forPort(grpcPort);
        for (BindableService grpcService : grpcServices) {
            serverBuilder.addService(grpcService);
        }
        serverBuilder.addService(ProtoReflectionService.newInstance());
        Server server = serverBuilder.build();
        server.start();
        server.awaitTermination();
    }
}
