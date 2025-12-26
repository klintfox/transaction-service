# transaction-service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/transaction-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- Apache Kafka Client ([guide](https://quarkus.io/guides/kafka)): Connect to Apache Kafka with its native API
- SmallRye Health ([guide](https://quarkus.io/guides/smallrye-health)): Monitor service health
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, Jakarta Persistence)
- SmallRye JWT ([guide](https://quarkus.io/guides/security-jwt)): Secure your applications with JSON Web Token
- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- SmallRye Metrics ([guide](https://quarkus.io/guides/smallrye-metrics)): Expose metrics for your services

## Architecture

This microservice is part of a distributed banking system. The architecture consists of three independent microservices:

- **customer-service**: Manages customer data and exposes endpoints for customer queries.
- **account-service**: Manages bank accounts, balances, and exposes endpoints for account queries and updates. It consumes Kafka events to update balances and audit logs asynchronously.
- **transaction-service** (this service): Orchestrates money transfers, publishes transaction events to Kafka, and records transaction history.

## Available Endpoints

- `POST /transactions`: Crea una nueva transacción entre cuentas.
- `GET /transactions/{id}`: Obtiene una transacción por su UUID.
- `GET /transactions`: Lista todas las transacciones.

> Todos los endpoints requieren autenticación JWT y roles `USER` o `ADMIN`.

## Usage Examples

### Test Credentials

To generate token follow this steps: https://github.com/klintfox/account-service

You must include a valid JWT token in the `Authorization` header for all requests. Example:
```
Authorization: Bearer <jwt_token>
```

### Crear Transacción
**Request:**
```json
POST /transactions
{
  "accountFrom": "1234567890",
  "accountTo": "1234567891",
  "amount": 100.00,
  "description": "Transferencia de prueba 1"
}
```
**Response:**
```json
{
  "id": "uuid",
  "accountFrom": "1234567890",
  "accountTo": "1234567891",
  "amount": 100.00,
  "transactionDate": "2025-12-23T22:37:45.1824947",
  "status": "COMPLETED",
  "description": "Transferencia de prueba 1"
}
```

### Consultar Transacción
**Request:**
```
GET /transactions/{id}
```
**Response:**
```json
{
  "id": "uuid",
  "accountFrom": "1234567890",
  "accountTo": "1234567891",
  "amount": 100.00,
  "transactionDate": "2025-12-23T22:37:45.1824947",
  "status": "COMPLETED",
  "description": "Transferencia de prueba 1"
}
```

## Evidences

![Create Transaction](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/1.PNG)

![Get Transaction by Id](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/2.PNG)

![All Transactions](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/3.PNG)

![Complete Transaction Kafka](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/4.PNG)

![Validation Not Same Accounts](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/e1.PNG)

![Validation Account Not Exits or Disable](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/e2.PNG)

![Failed Transaction Kafka](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/t1.PNG)

![Databse Transactions](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/t2.PNG)

![Consumer from Account-Service And Save Data](https://github.com/klintfox/account-service/blob/master/src/main/resources/evidencias/t3.PNG)