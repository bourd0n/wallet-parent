package com.bourd0n.wallet.server.service;

import com.bourd0n.wallet.api.grpc.BalanceRequest;
import com.bourd0n.wallet.api.grpc.BalanceResponse;
import com.bourd0n.wallet.api.grpc.BalanceServiceGrpc;
import com.bourd0n.wallet.server.model.User;
import com.bourd0n.wallet.server.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService extends BalanceServiceGrpc.BalanceServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        //todo: validate request
        long userId = request.getUserId();
        User user = userRepository.findById(userId)
                //todo: exception
                .orElseThrow(RuntimeException::new);
        BalanceResponse.Builder responseBuilder = BalanceResponse.newBuilder();

        user.getAccounts()
                .forEach(a -> responseBuilder.putMoneyAmount(a.getCurrencyCode(), a.getAmount().doubleValue()));

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
