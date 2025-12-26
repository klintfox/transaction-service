-- Script de creación de tablas para transaction-service
-- Base de datos: transaction_service_db

CREATE TABLE IF NOT EXISTS transaction (
    id UUID PRIMARY KEY,
    account_from VARCHAR(30) NOT NULL,
    account_to VARCHAR(30) NOT NULL,
    amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255)
);

-- Índices para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_transaction_account_from ON transaction(account_from);
CREATE INDEX IF NOT EXISTS idx_transaction_account_to ON transaction(account_to);
CREATE INDEX IF NOT EXISTS idx_transaction_date ON transaction(transaction_date);
