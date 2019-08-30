package com.bourd0n.wallet.server.repository;

import com.bourd0n.wallet.server.model.Account;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT a FROM Account a WHERE a.id = ?1")
    Optional<Account> lock(Long id);
}
