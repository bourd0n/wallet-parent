package com.bourd0n.wallet.server.service;

import com.bourd0n.wallet.api.grpc.CreateUserRequest;
import com.bourd0n.wallet.api.grpc.CreateUserResponse;
import com.bourd0n.wallet.api.grpc.UserServiceGrpc;
import com.bourd0n.wallet.server.model.Account;
import com.bourd0n.wallet.server.model.User;
import com.bourd0n.wallet.server.repository.AccountRepository;
import com.bourd0n.wallet.server.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        Map<String, Double> moneyAmount = request.getMoneyAmount();
        User user = new User();
        Set<Account> accounts = moneyAmount.entrySet().stream()
                //todo validate currency
                //todo: currency from double
                .map(amountEntry -> new Account(user, amountEntry.getKey(), new BigDecimal(amountEntry.getValue())))
                .collect(Collectors.toSet());
        user.setAccounts(accounts);
        userRepository.save(user);

        responseObserver.onNext(CreateUserResponse.newBuilder()
                .setUserId(user.getId())
                .build());

        responseObserver.onCompleted();
    }
}
