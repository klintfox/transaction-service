package com.tuempresa.transactionservice.service;

import com.tuempresa.transactionservice.client.AccountDTO;
import com.tuempresa.transactionservice.client.AccountServiceClient;
import com.tuempresa.transactionservice.dto.TransactionCompletedEvent;
import com.tuempresa.transactionservice.dto.TransactionFailedEvent;
import com.tuempresa.transactionservice.dto.TransactionRequestDTO;
import com.tuempresa.transactionservice.dto.TransactionResponseDTO;
import com.tuempresa.transactionservice.model.Transaction;
import com.tuempresa.transactionservice.repository.TransactionRepository;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TransactionService {

    private static final Logger Log = Logger.getLogger(TransactionService.class);

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    @Channel("transactions-completed")
    Emitter<TransactionCompletedEvent> completedEmitter;

    @Inject
    @Channel("transactions-failed")
    Emitter<TransactionFailedEvent> failedEmitter;

    @Inject
    @RestClient
    AccountServiceClient accountServiceClient;

    @Inject
    TransactionPersistenceService persistenceService;

    @Context
    HttpHeaders httpHeaders;

    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        Log.info("Begginning creation of transaction from " + request.getAccountFrom() + " to "
                + request.getAccountTo() + " amount " + request.getAmount());

        String jwt = httpHeaders.getHeaderString("Authorization");
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAccountFrom(request.getAccountFrom());
        transaction.setAccountTo(request.getAccountTo());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDescription(request.getDescription());

        String failReason = null;
        boolean valid = true;

        // Validaciones y llamadas REST fuera de la transacción
        try {
            AccountDTO source = accountServiceClient.getAccountByNumber(request.getAccountFrom(), jwt);
            AccountDTO dest = accountServiceClient.getAccountByNumber(request.getAccountTo(), jwt);
            if (source == null || !source.isActive()) {
                failReason = "Cuenta origen no existe o está inactiva";
                valid = false;
            } else if (dest == null || !dest.isActive()) {
                failReason = "Cuenta destino no existe o está inactiva";
                valid = false;
            } else if (request.getAccountFrom().equals(request.getAccountTo())) {
                failReason = "Las cuentas no pueden ser la misma";
                valid = false;
            } else if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                failReason = "El monto debe ser mayor a cero";
                valid = false;
            } else if (source.getBalance().compareTo(request.getAmount()) < 0) {
                failReason = "Saldo insuficiente en cuenta origen";
                valid = false;
            }
        } catch (Exception e) {
            failReason = "Error consultando cuentas: " + e.getMessage();
            valid = false;
        }

        if (valid) {
            transaction.setStatus("COMPLETED");
        } else {
            transaction.setStatus("FAILED");
            transaction.setDescription(failReason);
        }
        persistenceService.persistTransaction(transaction);
        publishTransactionEvent(transaction, failReason);
        return toResponseDTO(transaction);
    }

    private void publishTransactionEvent(Transaction transaction, String reason) {
        if ("COMPLETED".equals(transaction.getStatus())) {
            TransactionCompletedEvent event = toCompletedEvent(transaction);
            completedEmitter.send(event);
        } else if ("FAILED".equals(transaction.getStatus())) {
            TransactionFailedEvent event = toFailedEvent(transaction, reason != null ? reason : "Validación fallida");
            failedEmitter.send(event);
        }
    }

    private TransactionCompletedEvent toCompletedEvent(Transaction transaction) {
        TransactionCompletedEvent event = new TransactionCompletedEvent();
        event.setTransactionId(transaction.getId().toString());
        event.setSourceAccountNumber(transaction.getAccountFrom());
        event.setDestinationAccountNumber(transaction.getAccountTo());
        event.setAmount(transaction.getAmount());
        event.setTimestamp(transaction.getTransactionDate());
        event.setDescription(transaction.getDescription());
        return event;
    }

    private TransactionFailedEvent toFailedEvent(Transaction transaction, String reason) {
        TransactionFailedEvent event = new TransactionFailedEvent();
        event.setTransactionId(transaction.getId().toString());
        event.setSourceAccountNumber(transaction.getAccountFrom());
        event.setDestinationAccountNumber(transaction.getAccountTo());
        event.setAmount(transaction.getAmount());
        event.setTimestamp(transaction.getTransactionDate());
        event.setReason(reason);
        return event;
    }

    public Optional<TransactionResponseDTO> getTransactionById(UUID id) {
        return transactionRepository.findById(id).map(this::toResponseDTO);
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private TransactionResponseDTO toResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAccountFrom(transaction.getAccountFrom());
        dto.setAccountTo(transaction.getAccountTo());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setStatus(transaction.getStatus());
        dto.setDescription(transaction.getDescription());
        return dto;
    }
}
