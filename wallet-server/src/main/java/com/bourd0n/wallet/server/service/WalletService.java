package com.bourd0n.wallet.server.service;

import com.bourd0n.wallet.api.grpc.BalanceRequest;
import com.bourd0n.wallet.api.grpc.BalanceResponse;
import com.bourd0n.wallet.api.grpc.MoneyRequest;
import com.bourd0n.wallet.api.grpc.WalletServiceGrpc;
import com.bourd0n.wallet.server.exception.AccountNotFoundException;
import com.bourd0n.wallet.server.exception.InsufficientFundsException;
import com.bourd0n.wallet.server.exception.UserNotFoundException;
import com.bourd0n.wallet.server.model.Account;
import com.bourd0n.wallet.server.model.User;
import com.bourd0n.wallet.server.repository.AccountRepository;
import com.bourd0n.wallet.server.repository.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService extends WalletServiceGrpc.WalletServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);

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
        try {
            LOGGER.debug("Deposit request: {}", request);
            validateMoneyRequest(request);
            long userId = request.getUserId();
            Account account = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId))
                    .getAccounts()
                    .stream()
                    .filter(a -> a.getCurrencyCode().equals(request.getCurrency().name()))
                    .findFirst()
                    //lock
                    .flatMap(a -> accountRepository.lockForWrite(a.getId()))
                    .orElseThrow(() -> new AccountNotFoundException(userId, request.getCurrency()));

            account.setAmount(account.getAmount().add(new BigDecimal(request.getAmount())));
            accountRepository.save(account);

            LOGGER.info("Deposit request successful: {}", request);
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException | UserNotFoundException | AccountNotFoundException e) {
            LOGGER.error("Deposit request '" + request + "' failed", e);
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            LOGGER.error("Deposit request '" + request + "' failed", e);
            responseObserver.onError(Status.UNKNOWN.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional
    public void withdraw(MoneyRequest request, StreamObserver<Empty> responseObserver) {
        try {
            LOGGER.debug("Withdraw request: {}", request);
            validateMoneyRequest(request);
            long userId = request.getUserId();
            Account account = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId))
                    .getAccounts()
                    .stream()
                    .filter(a -> a.getCurrencyCode().equals(request.getCurrency().name()))
                    .findFirst()
                    //lock
                    .flatMap(a -> accountRepository.lockForWrite(a.getId()))
                    .orElseThrow(() -> new AccountNotFoundException(userId, request.getCurrency()));

            BigDecimal finalAmount = account.getAmount().subtract(new BigDecimal(request.getAmount()));
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException(request.getUserId(), request.getAmount(), request.getCurrency());
            }
            account.setAmount(finalAmount);
            accountRepository.save(account);

            LOGGER.info("Withdraw request successful: {}", request);
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException | UserNotFoundException | AccountNotFoundException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (InsufficientFundsException e) {
            LOGGER.error("Withdraw request '" + request + "' failed", e);
            responseObserver.onError(Status.FAILED_PRECONDITION.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            LOGGER.error("Withdraw request '" + request + "' failed", e);
            responseObserver.onError(Status.UNKNOWN.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        try {
            LOGGER.debug("Balance request: {}", request);
            long userId = request.getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            BalanceResponse.Builder responseBuilder = BalanceResponse.newBuilder();

            user.getAccounts()
                    .forEach(a -> responseBuilder.putMoneyAmount(a.getCurrencyCode(), a.getAmount().doubleValue()));

            BalanceResponse response = responseBuilder.build();
            LOGGER.info("Balance request '{}' successful. Result: {}.", request, response);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException | UserNotFoundException e) {
            LOGGER.error("BalanceRequest request '" + request + "' failed", e);
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            LOGGER.error("BalanceRequest request '" + request + "' failed", e);
            responseObserver.onError(Status.UNKNOWN.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private void validateMoneyRequest(MoneyRequest request) {
        if (new BigDecimal(request.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount can't be < 0, but " + request.getAmount() + " was passed");
        }
    }
}
