package com.tuempresa.transactionservice.dto;
import java.math.BigDecimal;

public class TransactionRequestDTO {

    private String description;
    private BigDecimal amount;
    private String accountTo;
    private String accountFrom;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountFrom() {
        return accountFrom;
    }
}



