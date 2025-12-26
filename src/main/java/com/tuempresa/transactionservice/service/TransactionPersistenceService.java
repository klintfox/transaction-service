package com.tuempresa.transactionservice.service;

import com.tuempresa.transactionservice.model.Transaction;
import com.tuempresa.transactionservice.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TransactionPersistenceService {
    @Inject
    TransactionRepository transactionRepository;

    @Transactional
    public void persistTransaction(Transaction transaction) {
        transactionRepository.persist(transaction);
    }
}

