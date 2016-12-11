package pl.poznan.put.bsr.bank.utils;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import pl.poznan.put.bsr.bank.models.Counter;

/**
 * @author Kamil Walkowiak
 */
public class DataStoreHandlerUtil {
    private static DataStoreHandlerUtil Instance = new DataStoreHandlerUtil();
    private Datastore dataStore;

    public void initializeDataStore() {
        final Morphia morphia = new Morphia();
        dataStore = morphia.createDatastore(new MongoClient("localhost", ConstantsUtil.MONGODB_PORT), "bank");
        morphia.mapPackage("pl.poznan.put.bsr.bank.models");
        dataStore.ensureIndexes();

        if(dataStore.getCount(Counter.class) == 0) {
            dataStore.save(new Counter("accountNoCounter"));
        }
    }

    public static DataStoreHandlerUtil getInstance() {
        return Instance;
    }

    public Datastore getDataStore() {
        return dataStore;
    }
}
