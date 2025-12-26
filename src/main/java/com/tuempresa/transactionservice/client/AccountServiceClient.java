package com.tuempresa.transactionservice.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "account-service-api")
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public interface AccountServiceClient {

    @GET
    @Path("/{accountNumber}")
    AccountDTO getAccountByNumber(@PathParam("accountNumber") String accountNumber,
                                  @HeaderParam("Authorization") String authorization);
}

