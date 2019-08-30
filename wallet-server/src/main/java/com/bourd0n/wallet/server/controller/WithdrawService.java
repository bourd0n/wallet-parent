package com.bourd0n.wallet.server.controller;

import com.bourd0n.wallet.api.grpc.MoneyRequest;
import com.bourd0n.wallet.api.grpc.WithdrawServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class WithdrawService extends WithdrawServiceGrpc.WithdrawServiceImplBase {

    @Override
    public void withdraw(MoneyRequest request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
