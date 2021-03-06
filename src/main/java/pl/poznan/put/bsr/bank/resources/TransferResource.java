package pl.poznan.put.bsr.bank.resources;

import org.json.simple.JSONObject;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.bankOperations.Transfer;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class responsible for receiving external transfer to this bank
 * @author Kamil Walkowiak
 */
@Path("/transfer")
public class TransferResource {
    /**
     * Receive external transfer directed to account in this bank
     * @param transfer transfer object serialized from request JSON content
     * @return response status 201
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeTransfer(@NotNull Transfer transfer) {
        validateTransfer(transfer);
        prepareTransfer(transfer);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        BankAccount targetBankAccount = datastore.find(BankAccount.class).field("accountNo")
                .equal(transfer.getTargetAccountNo()).get();
        if (targetBankAccount == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("{\n" +
                    "  \"error\": \"target bank account not exists\"\n" +
                    "}").build());
        }

        try {
            transfer.doOperation(targetBankAccount);
        } catch (BankOperationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\n" +
                    "  \"error\": \"unknown error\"\n" +
                    "}").build());
        }
        datastore.save(targetBankAccount);

        return Response.created(null).build();
    }

    private void validateTransfer(Transfer transfer) {
        String errorMessage = "";

        if (transfer.getAmount() <= 0.0 || transfer.getAmount() > 1000000) {
            errorMessage += "amount,";
        }
        if (transfer.getTitle() == null || transfer.getTitle().length() == 0) {
            errorMessage += "title,";
        }
        if (transfer.getSourceAccountNo() == null || !transfer.getSourceAccountNo().matches("[0-9]+") ||
                transfer.getSourceAccountNo().length() != 26 ||
                !BankAccount.validateCheckSum(transfer.getSourceAccountNo())) {
            errorMessage += "sender_account,";
        }
        if (transfer.getTargetAccountNo() == null ||
                !(transfer.getTargetAccountNo().matches("[0-9]+") && transfer.getTargetAccountNo().length() == 26)) {
            errorMessage += "receiver_account,";
        }

        if (errorMessage.length() > 0) {
            errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            errorMessage += " is missing or invalid";
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\n" +
                    "  \"error\": \"" + errorMessage + "\"\n" +
                    "}").build());
        }

        if (transfer.getSourceAccountNo().equals(transfer.getTargetAccountNo())) {
            errorMessage = "sender_account is the same as receiver_account";
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\n" +
                    "  \"error\": \"" + errorMessage + "\"\n" +
                    "}").build());
        }
    }

    private void prepareTransfer(Transfer transfer) {
        transfer.setTitle(JSONObject.escape(transfer.getTitle()));
        transfer.setExecuted(false);
        transfer.setAmount(transfer.getAmount() / 100);
        transfer.setDirection(Transfer.TransferDirection.IN);
    }
}
