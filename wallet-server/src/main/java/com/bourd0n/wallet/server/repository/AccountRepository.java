package com.bourd0n.wallet.server.repository;

import com.bourd0n.wallet.server.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

}
