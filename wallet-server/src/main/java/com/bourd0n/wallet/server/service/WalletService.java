package com.bourd0n.wallet.server.service;

import com.bourd0n.wallet.api.grpc.BalanceRequest;
import com.bourd0n.wallet.api.grpc.BalanceResponse;
import com.bourd0n.wallet.api.grpc.MoneyRequest;
import com.bourd0n.wallet.api.grpc.WalletServiceGrpc;
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
public class WalletService extends WalletServiceGrpc.WalletServiceImplBase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public WalletService(UserRepository userRepository, AccountRepository accountRepository) {
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

    @Override
    @Transactional
    public void withdraw(MoneyRequest request, StreamObserver<Empty> responseObserver) {
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

        BigDecimal finalAmount = account.getAmount().subtract(new BigDecimal(request.getAmount()));
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            //todo: exception
            throw new RuntimeException();
        }
        account.setAmount(finalAmount);
        accountRepository.save(account);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
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
