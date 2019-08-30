package com.bourd0n.wallet.server.controller;

import com.bourd0n.wallet.api.grpc.BalanceRequest;
import com.bourd0n.wallet.api.grpc.BalanceResponse;
import com.bourd0n.wallet.api.grpc.BalanceServiceGrpc;
import com.bourd0n.wallet.api.grpc.CurrencyType;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class BalanceService extends BalanceServiceGrpc.BalanceServiceImplBase {

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        responseObserver.onNext(BalanceResponse.newBuilder()
                .putMoneyAmount(CurrencyType.EUR.name(), 900)
                .build());
        responseObserver.onCompleted();
    }
}
