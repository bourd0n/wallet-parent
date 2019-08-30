package com.bourd0n.wallet.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "currency_code")
    private String currencyCode;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Version
    @Column(name = "version")
    @JsonIgnore
    private int version;


    private Account() {
    }

    public Account(User user, String currencyCode) {
        this.user = user;
        this.currencyCode = currencyCode;
        this.amount = BigDecimal.ZERO;
    }

    public Account(User user, String currencyCode, BigDecimal amount) {
        this.user = user;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @JsonIgnore
    public CurrencyUnit getCurrency() {
        return Monetary.getCurrency(currencyCode);
    }

    @JsonIgnore
    public MonetaryAmount getMoney() {
        return Money.of(amount, currencyCode);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return version == account.version &&
                Objects.equals(id, account.id) &&
                Objects.equals(amount, account.amount) &&
                Objects.equals(currencyCode, account.currencyCode) &&
                Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currencyCode, user, version);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", user=" + user +
                ", version=" + version +
                '}';
    }
}
