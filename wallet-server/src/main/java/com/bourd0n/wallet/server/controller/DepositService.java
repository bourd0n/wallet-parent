package com.bourd0n.wallet.server.controller;

import com.bourd0n.wallet.api.grpc.DepositServiceGrpc;
import com.bourd0n.wallet.api.grpc.MoneyRequest;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class DepositService extends DepositServiceGrpc.DepositServiceImplBase {
    @Override
    public void deposit(MoneyRequest request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
