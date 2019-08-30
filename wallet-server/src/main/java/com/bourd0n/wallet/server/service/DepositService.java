package com.bourd0n.wallet.server.service;

import com.bourd0n.wallet.api.grpc.DepositServiceGrpc;
import com.bourd0n.wallet.api.grpc.MoneyRequest;
import com.bourd0n.wallet.server.model.Account;
import com.bourd0n.wallet.server.model.User;
import com.bourd0n.wallet.server.repository.AccountRepository;
import com.bourd0n.wallet.server.repository.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DepositService extends DepositServiceGrpc.DepositServiceImplBase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public DepositService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void deposit(MoneyRequest request, StreamObserver<Empty> responseObserver) {
        //todo: validate request
        long userId = request.getUserId();
        User user = userRepository.findById(userId)
                //todo: exception
                .orElseThrow(RuntimeException::new);
        Account account = user.getAccounts()
                .stream()
                .filter(a -> a.getCurrencyCode().equals(request.getCurrency().name()))
                .findFirst()
                //lock
                .flatMap(a -> accountRepository.lockForWrite(a.getId()))
                //todo: exception
                .orElseThrow(RuntimeException::new);

        account.setAmount(account.getAmount().add(new BigDecimal(request.getAmount())));
        accountRepository.save(account);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
