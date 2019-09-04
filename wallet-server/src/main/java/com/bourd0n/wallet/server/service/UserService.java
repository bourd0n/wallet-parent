package com.bourd0n.wallet.server.service;

import com.bourd0n.wallet.api.grpc.CreateUserRequest;
import com.bourd0n.wallet.api.grpc.CreateUserResponse;
import com.bourd0n.wallet.api.grpc.CurrencyType;
import com.bourd0n.wallet.api.grpc.UserServiceGrpc;
import com.bourd0n.wallet.server.model.Account;
import com.bourd0n.wallet.server.model.User;
import com.bourd0n.wallet.server.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        try {
            validateCreateUserRequest(request);
            LOGGER.debug("Request for user create : '{}'", request);
            Map<String, Double> moneyAmount = request.getMoneyAmount();
            User user = new User();
            Set<Account> accounts = moneyAmount.entrySet().stream()
                    .map(amountEntry -> new Account(user, amountEntry.getKey(), new BigDecimal(amountEntry.getValue())))
                    .collect(Collectors.toSet());
            user.setAccounts(accounts);
            userRepository.save(user);

            responseObserver.onNext(CreateUserResponse.newBuilder()
                    .setUserId(user.getId())
                    .build());

            LOGGER.info("User with id {} created", user.getId());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            LOGGER.error("User create request '" + request + "' failed", e);
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            LOGGER.error("User create request '" + request + "' failed", e);
            responseObserver.onError(Status.UNKNOWN.withCause(e)
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        for (Map.Entry<String, Double> moneyAmountEntry : request.getMoneyAmount().entrySet()) {
            try {
                String key = moneyAmountEntry.getKey();
                CurrencyType.valueOf(key);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown currency " + moneyAmountEntry.getKey());
            }
            if (new BigDecimal(moneyAmountEntry.getValue()).compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Money amount can't be < 0, but " + moneyAmountEntry.getValue() + " was passed");
            }
        }
    }
}
