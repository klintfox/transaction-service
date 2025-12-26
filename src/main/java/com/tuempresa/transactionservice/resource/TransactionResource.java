package com.tuempresa.transactionservice.resource;

import com.tuempresa.transactionservice.dto.ErrorResponse;
import com.tuempresa.transactionservice.dto.TransactionRequestDTO;
import com.tuempresa.transactionservice.dto.TransactionResponseDTO;
import com.tuempresa.transactionservice.service.TransactionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private static final Logger Log = Logger.getLogger(TransactionResource.class);

    @Inject
    TransactionService transactionService;

    @POST
    @RolesAllowed({"USER", "ADMIN"})
    public Response createTransaction(TransactionRequestDTO request) {
        Log.info("Received request to create transaction from " + request.getAccountFrom()
                + " to " + request.getAccountTo() + " amount " + request.getAmount());

        TransactionResponseDTO response = transactionService.createTransaction(request);
        if ("FAILED".equals(response.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("No se pudo crear la transacción: " + response.getDescription()))
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getTransactionById(@PathParam("id") UUID id) {
        Optional<TransactionResponseDTO> transaction = transactionService.getTransactionById(id);
        return transaction.map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}
