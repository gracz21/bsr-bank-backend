package pl.poznan.put.bsr.bank.resources;

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
 * @author Kamil Walkowiak
 */
@Path("/transfer")
public class TransferResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeTransfer(@NotNull Transfer transfer) throws BankOperationException {
        validateTransferCompleteness(transfer);
        prepareTransfer(transfer);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        BankAccount targetBankAccount = datastore.find(BankAccount.class).field("accountNo")
                .equal(transfer.getTargetAccountNo()).get();
        if(targetBankAccount == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("{\n" +
                    "  \"error\": \"target bank account not exists\"\n" +
                    "}").build());
        }

        transfer.doOperation(targetBankAccount);
        datastore.save(targetBankAccount);

        return Response.created(null).build();
    }

    private void validateTransferCompleteness(Transfer transfer) {
        String missingFields = "";

        if(transfer.getAmount() == 0.0) {
            missingFields += "amount,";
        }
        if(transfer.getTitle() == null || transfer.getTitle().length() == 0) {
            missingFields += "title,";
        }
        if(transfer.getSourceAccountNo() == null || transfer.getSourceAccountNo().length() == 0) {
            missingFields += "sender_account,";
        }
        if(transfer.getTargetAccountNo() == null || transfer.getTargetAccountNo().length() == 0) {
            missingFields += "receiver_account,";
        }

        if(missingFields.length() > 0) {
            missingFields = missingFields.substring(0, missingFields.length() -1);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\n" +
                    "  \"error\": \"" + missingFields + " is missing\"\n" +
                    "}").build());
        }
    }

    private void prepareTransfer(Transfer transfer) {
        if(transfer.getAmount() < 0.0) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("{\n" +
                    "  \"error\": \"amount is negative\"\n" +
                    "}").build());
        }
        transfer.setExecuted(false);
        transfer.setAmount(transfer.getAmount()/100);
        transfer.setDirection(Transfer.TransferDirection.IN);
    }
}
