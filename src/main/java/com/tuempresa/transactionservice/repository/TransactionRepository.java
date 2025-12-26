package com.tuempresa.transactionservice.repository;

import com.tuempresa.transactionservice.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class TransactionRepository {
    @PersistenceContext
    EntityManager em;

    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(em.find(Transaction.class, id));
    }

    public List<Transaction> findAll() {
        return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
    }

    @Transactional
    public void persist(Transaction transaction) {
        em.persist(transaction);
    }

    @Transactional
    public Transaction update(Transaction transaction) {
        return em.merge(transaction);
    }
}

